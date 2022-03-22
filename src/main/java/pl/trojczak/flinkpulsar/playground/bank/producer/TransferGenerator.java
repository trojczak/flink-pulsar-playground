package pl.trojczak.flinkpulsar.playground.bank.producer;

import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.INCOMING_TRANSFERS_TOPIC;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.PULSAR_SERVICE_URL;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.SLEEP_TIME_IN_SECONDS;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import pl.trojczak.flinkpulsar.playground.bank.common.Bank;
import pl.trojczak.flinkpulsar.playground.bank.model.Transfer;
import pl.trojczak.flinkpulsar.playground.bank.util.RandomUtil;

public class TransferGenerator {

    public static void main(String[] args) throws PulsarClientException {
        var transferGenerator = new TransferGenerator();
        transferGenerator.runGenerator();
    }

    public void runGenerator() throws PulsarClientException {
        var pulsarClient = PulsarClient.builder().serviceUrl(PULSAR_SERVICE_URL.getValue()).build();
        try (var transfersProducer = pulsarClient
                .newProducer(Schema.AVRO(Transfer.class))
                .topic(INCOMING_TRANSFERS_TOPIC.getValue())
                .create()) {
            while (true) {
                var transfer = generateTransfer();
                System.out.println("Sending incoming transfer: " + transfer);
                transfersProducer.send(transfer);

                Thread.sleep(Integer.parseInt(SLEEP_TIME_IN_SECONDS.getValue()));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        pulsarClient.close();
    }

    private Transfer generateTransfer() {
        Bank sourceBank = Bank.getRandom();
        Bank targetBank = Bank.getRandom();
        return new Transfer(sourceBank.name(), targetBank.name(), RandomUtil.getRandomInt(1, 10000), new Date());
    }
}
