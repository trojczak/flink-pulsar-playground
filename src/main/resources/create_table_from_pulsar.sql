CREATE TABLE ForbiddenSources
(
    `bank` STRING
) WITH (
      'connector' = 'pulsar',
      'topic' = 'persistent://public/default/forbidden-sources',
      'service-url' = 'pulsar://localhost:6650',
      'admin-url' = 'http://localhost:8080',
      'scan.startup.mode' = 'earliest',
--       'properties.pulsar.reader.readername' = 'forbidden-sources-reader',
      'format' = 'avro'

--       'connector.type' = 'pulsar',
--       'connector.version' = '1',
--       'connector.topic' = 'persistent://public/default/forbidden-sources',
--       'connector.service-url' = 'pulsar://localhost:6650',
--       'connector.admin-url' = 'http://localhost:8080',
--       'connector.startup-mode' = 'earliest',
--       'connector.properties.0.key' = 'pulsar.reader.readerName',
--       'connector.properties.0.value' = 'forbidden-sources-reader',
--       'format.type' = 'avro',
--       'update-mode' = 'append'
      )