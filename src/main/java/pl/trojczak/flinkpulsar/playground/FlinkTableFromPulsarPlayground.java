package pl.trojczak.flinkpulsar.playground;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

public class FlinkTableFromPulsarPlayground {

    public static void main(String[] args) throws Exception {
        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance()
                .inStreamingMode()
//                .inBatchMode()
                .build();
        TableEnvironment tableEnvironment = TableEnvironment.create(environmentSettings);

        var createTable = FlinkPulsarPlayground.class.getResourceAsStream("/create_table_from_pulsar.sql");
        var createTableFromPulsar = new String(createTable.readAllBytes(), StandardCharsets.UTF_8);

        tableEnvironment.executeSql(createTableFromPulsar);

        TableResult userTableResult = tableEnvironment.executeSql("SELECT * FROM ForbiddenSources");
        try (var users = userTableResult.collect()) {
            while (users.hasNext()) {
                var user = users.next();
                System.out.println(user);
            }
        }

        System.out.println("tutaj");
    }
}
