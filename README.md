# Flink Pulsar Playground

## Documents Project

### Creating schema

Copy `flink-pulsar-playground-1.0-SNAPSHOT.jar` to the `/pulsar/functions` directory within the Pulsar's cluster.

```shell
./pulsar-admin schemas delete persistent://public/default/documents
./pulsar-admin schemas extract \
    --classname pl.trojczak.flinkpulsar.playground.model.Document \
    --jar /pulsar/functions/flink-pulsar-playground-1.0-SNAPSHOT.jar \
    --type avro \
    persistent://public/default/documents
```

### Create topic

```shell
./pulsar-admin topics unload persistent://public/default/documents
./pulsar-admin topics delete persistent://public/default/documents
./pulsar-admin topics create persistent://public/default/documents
# infinite retention - keep forever even if acknowledged
./pulsar-admin topics set-retention -s -1 -t -1 persistent://public/default/documents
./pulsar-admin topics create-subscription persistent://public/default/documents -s documents-sub
```


## Banks Project

### Creating Schemas

Copy `flink-pulsar-playground-1.0-SNAPSHOT.jar` to the `/pulsar/functions` directory within the Pulsar's cluster.

```shell
INCOMING_TRANSFERS_TOPIC='persistent://public/default/incoming-transfers'
./pulsar-admin schemas delete $INCOMING_TRANSFERS_TOPIC
./pulsar-admin schemas extract \
    --classname pl.trojczak.flinkpulsar.playground.bank.model.Transfer \
    --jar /pulsar/functions/flink-pulsar-playground-1.0-SNAPSHOT.jar \
    --type avro \
    $INCOMING_TRANSFERS_TOPIC
    
APPROVED_TRANSFERS_TOPIC='persistent://public/default/approved-transfers'
./pulsar-admin schemas delete $APPROVED_TRANSFERS_TOPIC
./pulsar-admin schemas extract \
    --classname pl.trojczak.flinkpulsar.playground.bank.model.Transfer \
    --jar /pulsar/functions/flink-pulsar-playground-1.0-SNAPSHOT.jar \
    --type avro \
    $APPROVED_TRANSFERS_TOPIC
    
REJECTED_TRANSFERS_TOPIC='persistent://public/default/rejected-transfers'
./pulsar-admin schemas delete $REJECTED_TRANSFERS_TOPIC
./pulsar-admin schemas extract \
    --classname pl.trojczak.flinkpulsar.playground.bank.model.Transfer \
    --jar /pulsar/functions/flink-pulsar-playground-1.0-SNAPSHOT.jar \
    --type avro \
    $REJECTED_TRANSFERS_TOPIC
    
FORBIDDEN_SOURCES_TOPIC='persistent://public/default/forbidden-sources'
./pulsar-admin schemas delete $REJECTED_TRANSFERS_TOPIC
./pulsar-admin schemas extract \
    --classname pl.trojczak.flinkpulsar.playground.bank.model.Source \
    --jar /pulsar/functions/flink-pulsar-playground-1.0-SNAPSHOT.jar \
    --type avro \
    $FORBIDDEN_SOURCES_TOPIC
```

### Create topic

```shell
topics=("$INCOMING_TRANSFERS_TOPIC" "$APPROVED_TRANSFERS_TOPIC" "$REJECTED_TRANSFERS_TOPIC" "$FORBIDDEN_SOURCES_TOPIC")
for topic in ${topics[*]}; do
  ./pulsar-admin topics unload $topic;
  ./pulsar-admin topics delete $topic;
  ./pulsar-admin topics create $topic;
  # infinite retention - keep forever even if acknowledged
  ./pulsar-admin topics set-retention -s -1 -t -1 $topic;
done
  
./pulsar-admin topics create-subscription $INCOMING_TRANSFERS_TOPIC -s incoming-transfers-sub
./pulsar-admin topics create-subscription $APPROVED_TRANSFERS_TOPIC -s approved-transfers-sub
```