# Flink Pulsar Playground

## Creating schema

Copy `flink-pulsar-playground-1.0-SNAPSHOT.jar` to the `/pulsar/functions` directory within the Pulsar's cluster.

```
./pulsar-admin schemas delete persistent://public/default/documents
./pulsar-admin schemas extract \
    --classname pl.trojczak.flinkpulsar.playground.model.Document \
    --jar /pulsar/functions/flink-pulsar-playground-1.0-SNAPSHOT.jar \
    --type avro \
    persistent://public/default/documents
```

## Create topic

```
./pulsar-admin topics unload persistent://public/default/documents
./pulsar-admin topics delete persistent://public/default/documents
./pulsar-admin topics create persistent://public/default/documents
# infinite retention - keep forever even if acknowledged
./pulsar-admin topics set-retention -s -1 -t -1 persistent://public/default/documents
./pulsar-admin topics create-subscription persistent://public/default/documents -s documents-sub
```