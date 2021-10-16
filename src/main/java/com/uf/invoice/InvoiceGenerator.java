package com.uf.invoice;

import com.uf.data.Invoice;
import com.uf.data.Performance;
import com.uf.data.Play;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class InvoiceGenerator {

    public String statement(final Invoice invoice, final Map<String, Play> plays) {
        return new Statement(invoice, plays).renderPlain();
    }

    public String htmlStatement(Invoice invoice, Map<String, Play> plays) {
        return new Statement(invoice, plays).renderHTML();
    }

    private class StatementData {
        String customer;
        double totalAmount;
        int totalVolumeCredits;
        List<PerformanceExt> performances;
    }

    private class PerformanceExt extends Performance {
        Play play;
        double amount;
    }

    private class Statement {
        private Invoice invoice;
        private Map<String, Play> plays;
        final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        public Statement(Invoice invoice, Map<String, Play> plays) {
            this.invoice = invoice;
            this.plays = plays;
        }

        StatementData getStatementData() {
            StatementData data = new StatementData();
            data.performances = invoice.performances.stream().map(this::enrich).collect(Collectors.toList());
            data.customer = invoice.customer;
            data.totalAmount = totalAmount(data);
            data.totalVolumeCredits = totalVolumeCredit();
            return data;
        }

        private PerformanceExt enrich(Performance perf) {
            PerformanceExt ext = new PerformanceExt();
            ext.playID = perf.playID;
            ext.audience = perf.audience;
            ext.play = playFor(perf);
            ext.amount = amountFor(perf, ext.play);
            return ext;
        }

        public String renderPlain() {
            return renderPlainText(getStatementData());
        }

        public String renderHTML() {
            return renderHTML(getStatementData());
        }

        private String renderHTML(StatementData data) {
            StringBuilder result = new StringBuilder(format("<h1>Statement for %s</h1>\n", data.customer));
            result.append("<table>\n");
            result.append("<tr><th>play</th><th>seats</th><th>cost</th></tr>\n");
            for (PerformanceExt perf : data.performances) {
                if (!plays.containsKey(perf.playID))
                    throw new IllegalArgumentException("unknown type: " + perf.playID);
                result.append(format(" <tr><td>%s</td><td>%d</td><td>%s</td></tr>\n", playFor(perf).name, perf.audience, usd(amountFor(perf, playFor(perf)))));
            }
            result.append("</table>\n");
            result.append(format("<p>Amount owed is <em>%s</em></p>\n", usd(data.totalAmount)));
            result.append(format("<p>You earned <em>%d</em> credits</p>\n", data.totalVolumeCredits));
            return result.toString();
        }

        private String renderPlainText(StatementData data) {
            StringBuilder result = new StringBuilder(format("Statement for %s\n", data.customer));
            for (PerformanceExt perf : data.performances) {
                if (!plays.containsKey(perf.playID))
                    throw new IllegalArgumentException("unknown type: " + perf.playID);
                result.append(format(" %s: %s (%d seats)\n", playFor(perf).name, usd(amountFor(perf, playFor(perf))), perf.audience));
            }
            result.append(format("Amount owed is %s\n", usd(data.totalAmount)));
            result.append(format("You earned %d credits\n", data.totalVolumeCredits));
            return result.toString();
        }

        private double totalAmount(StatementData data) {
            var result = 0.0;
            for (PerformanceExt perf : data.performances) {
                result += perf.amount;
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
