package pl.trojczak.flinkpulsar.playground.pulsar;

import java.util.List;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import pl.trojczak.flinkpulsar.playground.model.Document;

public class PopulateDocuments {

    public static final String TOPIC = "persistent://public/default/documents";

    private static final String PULSAR_LOCAL_SERVICE_URL = "pulsar://localhost:6650";

    public static void main(String[] args) throws PulsarClientException {
        var pulsarClient = PulsarClient.builder().serviceUrl(PULSAR_LOCAL_SERVICE_URL).build();
        try (var documentProducer = pulsarClient
                .newProducer(Schema.AVRO(Document.class))
                .topic(TOPIC)
                .create()) {
            for (Document document : generateDocuments()) {
                documentProducer.send(document);
            }
        }

        pulsarClient.close();
    }

    private static List<Document> generateDocuments() {
        return List.of(
                new Document("1", "My Document", "This is a content"),
                new Document("2", "Hello World", "This is some other content"),
                new Document("3", "My Document #1", "Foo bar baz")
        );
    }
}