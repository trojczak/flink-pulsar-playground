package pl.trojczak.flinkpulsar.playground.bank.function;

import static pl.trojczak.flinkpulsar.playground.FlinkPulsarPlayground.PARTITION_DISCOVERY_INTERVAL;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.FORBIDDEN_SOURCES_TABLE;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.FORBIDDEN_SOURCES_TOPIC;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.INCOMING_TRANSFERS_TOPIC;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.PULSAR_ADMIN_URL;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.PULSAR_SERVICE_URL;

import java.io.Serializable;
import java.util.Properties;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarSource;
import org.apache.flink.streaming.connectors.pulsar.internal.AvroDeser;
import org.apache.flink.streaming.util.serialization.PulsarDeserializationSchema;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.CloseableIterator;
import org.apache.flink.util.Collector;
import pl.trojczak.flinkpulsar.playground.bank.model.Source;
import pl.trojczak.flinkpulsar.playground.bank.model.Transfer;

public class CheckTransfersFunction {

    public static void main(String[] args) throws Exception {
        var checkTransfersFunction = new CheckTransfersFunction();
        checkTransfersFunction.run();
    }

    public void run() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("topic", INCOMING_TRANSFERS_TOPIC.getValue());
        properties.setProperty("partition.discovery.interval-millis", PARTITION_DISCOVERY_INTERVAL);

        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        TableEnvironment executionEnvironment = TableEnvironment.create(EnvironmentSettings.newInstance().build());

        PulsarDeserializationSchema<Transfer> deserializationSchema = PulsarDeserializationSchema.valueOnly(AvroDeser.of(Transfer.class));
        FlinkPulsarSource<Transfer> transferSource = new FlinkPulsarSource<>(
                PULSAR_SERVICE_URL.getValue(),
                PULSAR_ADMIN_URL.getValue(),
                deserializationSchema,
                properties
        );
        transferSource.setStartFromEarliest();

        DataStreamSource<Transfer> transferStream = environment.addSource(transferSource);
        transferStream.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());

        ProcessFunction<Transfer, Transfer> checkTransfersFunction = new MyProcessFunction();
        transferStream.process(checkTransfersFunction);

        environment.execute();
    }

    private String prepareQueryString(Transfer transfer) {
        return "SELECT COUNT(*) FROM " + FORBIDDEN_SOURCES_TABLE.getValue() + " WHERE bank = " + transfer.getSource() + " OR bank = " + transfer.getTarget();
//                FORBIDDEN_SOURCES_TABLE.getValue(), transfer.getSource(), transfer.getTarget());
    }

    private static TableEnvironment buildTableEnvironment(StreamExecutionEnvironment environment, DataStream<Source> stream) {
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(environment);
        tableEnvironment.createTemporaryView(FORBIDDEN_SOURCES_TABLE.getValue(), stream);
        return tableEnvironment;
    }

    private class MyProcessFunction extends ProcessFunction<Transfer, Transfer> implements Serializable {

        @Override
        public void processElement(Transfer transfer, ProcessFunction<Transfer, Transfer>.Context ctx, Collector<Transfer> out) throws Exception {
            System.out.println("Transfer: " + transfer);

            var tableView = createTableView();
            var query =
                    "SELECT * FROM " + FORBIDDEN_SOURCES_TABLE.getValue() + " WHERE bank = '" + transfer.getSource() + "' OR bank = '" + transfer.getTarget() + "'";
            TableResult tableResult = tableView.executeSql(query);

            try (CloseableIterator<Row> iterator = tableResult.collect()) {
                System.out.println("I'm here!");
                if (iterator.hasNext()) {
                    Row row = iterator.next();
                    System.out.println(row.getField("bank"));
                }
            }
            System.out.println("After try");
        }

        private TableEnvironment createTableView() {
            Properties properties = new Properties();
            properties.setProperty("topic", FORBIDDEN_SOURCES_TOPIC.getValue());
            properties.setProperty("partition.discovery.interval-millis", PARTITION_DISCOVERY_INTERVAL);

            PulsarDeserializationSchema<Source> deserializationSchema = PulsarDeserializationSchema.valueOnly(AvroDeser.of(Source.class));
            FlinkPulsarSource<Source> sourcesSource = new FlinkPulsarSource<>(
                    PULSAR_SERVICE_URL.getValue(),
                    PULSAR_ADMIN_URL.getValue(),
                    deserializationSchema,
                    properties
            );
            sourcesSource.setStartFromEarliest();
            sourcesSource.setStartFromSubscription("my-forbidden-sources-sub");

            StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
            environment.setRuntimeMode(RuntimeExecutionMode.STREAMING);

            DataStream<Source> stream = environment.addSource(sourcesSource).name("Forbidden sources source");
            stream.assignTimestampsAndWatermarks(WatermarkStrategy.forMonotonousTimestamps());
            return buildTableEnvironment(environment, stream);
        }
    }
}
