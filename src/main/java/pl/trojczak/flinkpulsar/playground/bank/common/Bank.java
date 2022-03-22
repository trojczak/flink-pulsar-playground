package pl.trojczak.flinkpulsar.playground.bank.common;

import java.util.Arrays;
import java.util.List;

import pl.trojczak.flinkpulsar.playground.bank.util.RandomUtil;

public enum Bank {

    BNP_PARIBAS("BNP Paribas"),
    CREDIT_AGRICOLE("Crédit Agricole"),
    DEUTSCHE_BANK("Deutsche Bank"),
    ING_BANK("ING Bank"),
    RAIFFEISEN_BANK("Raiffeisen Bank"),
    BANK_OF_IRELAND("Bank of Ireland"),
    PKO_SA("Powszechna Kasa Oszczędności Bank Polski SA"),
    SANTANDER("Santander Consumer Bank"),
    ALFA_BANK("Alfa-Bank"),
    ROSBANK("Rosbank"),
    GAZPROMBANK("Gazprombank"),
    PRIVAT_BANK("PrivatBank"),
    UNICREDIT_BANK("UniCredit Bank"),
    BANK_OF_SCOTLAND("UniCredit Bank"),
    BARCLAYS("Barclays");

    public static Bank getRandom() {
        List<Bank> banks = Arrays.asList(values());
        return banks.get(RandomUtil.getRandomInt(values().length));
    }

    public static Bank getRandomOtherThan(Bank bank) {
        for (int i = 0; i < values().length; i++) {
            var candidateBank = getRandom();
            if (candidateBank != bank) {
                return candidateBank;
            }
        }
        throw new IllegalStateException(String.format("Unable to find other bank that '%s'. This shouldn't happen.", bank));
    }

    private final String title;

    Bank(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
