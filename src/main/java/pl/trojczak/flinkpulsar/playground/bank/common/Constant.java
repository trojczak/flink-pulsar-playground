package pl.trojczak.flinkpulsar.playground.bank.common;

public enum Constant {

    FORBIDDEN_SOURCES_TOPIC("persistent://public/default/forbidden-sources"),
    PULSAR_SERVICE_URL("pulsar://localhost:6650"),
    PULSAR_ADMIN_URL("http://localhost:8080"),
    SLEEP_TIME_IN_SECONDS("30000"),
    FORBIDDEN_SOURCES_TABLE("forbidden_sources1"),
    INCOMING_TRANSFERS_TOPIC("persistent://public/default/incoming-transfers");

    private final String value;

    Constant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
