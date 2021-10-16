package com.uf.invoice;

import com.uf.data.Invoice;
import com.uf.data.Performance;
import com.uf.data.Play;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import static java.lang.String.format;

public class InvoiceGenerator {

    public String statement(final Invoice invoice, final Map<String, Play> plays) {
        return new Generator(invoice, plays).renderText();
    }

    private class StatementData {
        String customer;
    }

    private class Generator {
        private Invoice invoice;
        private Map<String, Play> plays;
        final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        public Generator(Invoice invoice, Map<String, Play> plays) {
            this.invoice = invoice;
            this.plays = plays;
        }

        public String renderText() {
            StatementData data = new StatementData();
            data.customer = invoice.customer;
            return renderPlainText(data);
        }

        private String renderPlainText(StatementData data) {
            StringBuilder result = new StringBuilder(format("Statement for %s\n", data.customer));
            for (Performance perf : invoice.performances) {
                if (!plays.containsKey(perf.playID))
                    throw new IllegalArgumentException("unknown type: " + perf.playID);
                result.append(format(" %s: %s (%d seats)\n", playFor(perf).name, usd(amountFor(perf, playFor(perf))), perf.audience));
            }
            result.append(format("Amount owed is %s\n", usd(totalAmount())));
            result.append(format("You earned %d credits\n", totalVolumeCredit()));
            return result.toString();
        }

        private double totalAmount() {
            var result = 0.0;
            for (Performance perf : invoice.performances) {
                result += amountFor(perf, playFor(perf));
            }
            return result;
        }

        private int totalVolumeCredit() {
            var result = 0;
            for (Performance perf : invoice.performances) {
                result += volumeCreditFor(perf);
            }
            return result;
        }

        private String usd(double totalAmount) {
            return currency.format(totalAmount / 100.0);
        }

        private int volumeCreditFor(Performance perf) {
            int result = Math.max(perf.audience - 30, 0);
            if ("comedy".equals(playFor(perf).type))
                result += perf.audience / 5;
            return result;
        }

        private Play playFor(Performance perf) {
            return plays.get(perf.playID);
        }

        private double amountFor(Performance perf, Play play) {
            var thisAmount = 0.0;
            switch (play.type) {
                case "tragedy":
                    thisAmount = 40000.0;
                    if (perf.audience > 30) {
                        thisAmount += 1000 * (perf.audience - 30);
                    }
                    break;
                case "comedy":
                    thisAmount = 30000.0;
                    if (perf.audience > 20) {
                        thisAmount += 10000 + 500 * (perf.audience - 20);
                    }
                    thisAmount += 300 * perf.audience;
                    break;
                default:
                    throw new IllegalArgumentException("unknown type: " + play.type);
            }
            return thisAmount;
        }
    }
}
