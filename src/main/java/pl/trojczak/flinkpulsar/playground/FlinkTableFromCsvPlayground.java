package pl.trojczak.flinkpulsar.playground;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;

public class FlinkTableFromCsvPlayground {

    public static void main(String[] args) throws IOException {
        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
        TableEnvironment tableEnvironment = TableEnvironment.create(environmentSettings);

        var createTable = FlinkPulsarPlayground.class.getResourceAsStream("/create_table_from_csv.sql");
        var createTableFromCsv = new String(createTable.readAllBytes(), StandardCharsets.UTF_8);

        TableResult userTable = tableEnvironment.executeSql(createTableFromCsv);

        TableResult userTableResult = tableEnvironment.executeSql("SELECT * FROM Users");
        var users = userTableResult.collect();
        while (users.hasNext()) {
            var user = users.next();
            System.out.println(user);
        }
    }
}
