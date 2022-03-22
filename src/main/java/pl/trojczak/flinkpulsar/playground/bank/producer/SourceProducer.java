package pl.trojczak.flinkpulsar.playground.bank.producer;

import static pl.trojczak.flinkpulsar.playground.bank.common.Bank.ALFA_BANK;
import static pl.trojczak.flinkpulsar.playground.bank.common.Bank.GAZPROMBANK;
import static pl.trojczak.flinkpulsar.playground.bank.common.Bank.ROSBANK;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.FORBIDDEN_SOURCES_TOPIC;
import static pl.trojczak.flinkpulsar.playground.bank.common.Constant.PULSAR_SERVICE_URL;

import java.util.List;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import pl.trojczak.flinkpulsar.playground.bank.common.Bank;
import pl.trojczak.flinkpulsar.playground.bank.model.Source;

@Slf4j
public class SourceProducer {

    private static final List<Bank> FORBIDDEN_SOURCES = List.of(ALFA_BANK, ROSBANK, GAZPROMBANK);

    public static void main(String[] args) throws PulsarClientException {
        var sourceProducer = new SourceProducer();
        sourceProducer.run();
    }

    private void run() throws PulsarClientException {
        var pulsarClient = PulsarClient.builder().serviceUrl(PULSAR_SERVICE_URL.getValue()).build();
        try (var transfersProducer = pulsarClient
                .newProducer(Schema.AVRO(Source.class))
                .topic(FORBIDDEN_SOURCES_TOPIC.getValue())
                .create()) {

            var systemInScanner = new Scanner(System.in);

            for (Bank bank : FORBIDDEN_SOURCES) {
                log.info("Adding {} bank to forbidden sources topic...", bank);

                transfersProducer.send(new Source(bank.name()));

                System.out.println("Press <ENTER> to go further...");
                systemInScanner.nextLine();
            }
        }

        pulsarClient.close();
    }
}
