package pl.trojczak.flinkpulsar.playground.avro;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import pl.trojczak.flinkpulsar.playground.model.Document;

public class AvroPlayground {

    public static final String PATH = "output.txt";

    public static void main(String[] args) throws IOException {
        serializeObject();

        deserializeObject();

        test();
    }

    private static void serializeObject() throws IOException {
        ReflectDatumWriter<Document> documentWriter = new ReflectDatumWriter<>(Document.class);
        DataFileWriter<Document> documentFileWriter = new DataFileWriter<>(documentWriter);

        documentFileWriter.create(ReflectData.get().getSchema(Document.class), new File(PATH));
        for (Document document : generateDocuments()) {
            documentFileWriter.append(document);
        }
        documentFileWriter.close();
    }

    private static void deserializeObject() throws IOException {
        File file = new File(PATH);
        GenericDatumReader<Document> datumReader = new GenericDatumReader<>();
        DataFileReader<Document> dataFileReader = new DataFileReader<>(file, datumReader);

        while (dataFileReader.hasNext()) {
            System.out.println(dataFileReader.next());
        }

        dataFileReader.close();
    }

    private static void test() throws IOException {
        // TODO: Don't work...
//        byte[] message = "test".getBytes(StandardCharsets.UTF_8);
//        BinaryDecoder decoder = DecoderFactory.get().directBinaryDecoder()  //binaryDecoder(message, null);
        byte[] message = new byte[] {0x02, 0x22, 0x54, 0x68, 0x69, 0x73, 0x20, 0x69, 0x73, 0x20, 0x61, 0x20, 0x63, 0x6f, 0x6e, 0x74, 0x65, 0x6e, 0x74, 0x02, 0x02, 0x31, 0x02, 0x16, 0x4d, 0x79, 0x20, 0x44, 0x6f, 0x63, 0x75, 0x6d, 0x65, 0x6e, 0x74};

        SeekableByteArrayInput inputStream = new SeekableByteArrayInput(message);
        ReflectDatumReader<Document> datumReader = new ReflectDatumReader<>(Document.class);
        DataFileReader<Document> dataFileReader = new DataFileReader<>(inputStream, datumReader);
//        Document document = dataFileReader.read(null, decoder);

        while (dataFileReader.hasNext()) {
            System.out.println(dataFileReader.next());
        }
    }

    private static List<Document> generateDocuments() {
        return List.of(
                new Document("1", "My Document", "This is a content"),
                new Document("2", "Hello World", "This is some other content"),
                new Document("3", "My Document #1", "Foo bar baz")
        );
    }
}
