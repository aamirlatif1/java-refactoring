package com.uf.invoice;

import com.uf.data.*;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.uf.invoice.PerformanceCalculator.createCalculator;
import static java.lang.String.format;

public class InvoiceGenerator {

    public String plainStatement(final Invoice invoice, final Map<String, Play> plays) {
        return new Statement(invoice, plays).renderPlain();
    }

    public String htmlStatement(Invoice invoice, Map<String, Play> plays) {
        return new Statement(invoice, plays).renderHTML();
    }

    private static class Statement {
        private final Invoice invoice;
        private final Map<String, Play> plays;
        final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        public Statement(Invoice invoice, Map<String, Play> plays) {
            this.invoice = invoice;
            this.plays = plays;
        }

        StatementData getStatementData() {
            StatementData data = new StatementData();
            data.performances = invoice.performances.stream().map(this::enrichPerformance).collect(Collectors.toList());
            data.customer = invoice.customer;
            data.totalAmount = data.totalAmount();
            data.totalVolumeCredits = data.totalVolumeCredit();
            return data;
        }

        public PerformanceExt enrichPerformance(Performance aPerformance) {
            if (!plays.containsKey(aPerformance.playID))
                throw new IllegalArgumentException("unknown type: " + aPerformance.playID);
            PerformanceExt ext = new PerformanceExt();
            Play play = playFor(aPerformance);
            PerformanceCalculator calculator = createCalculator(aPerformance, play);
            ext.playID = aPerformance.playID;
            ext.audience = aPerformance.audience;
            ext.play = play;
            ext.amount = calculator.amount();
            ext.volumeCredit = calculator.volumeCredit();
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
                result.append(format(" <tr><td>%s</td><td>%d</td><td>%s</td></tr>\n", playFor(perf).name, perf.audience, usd(perf.amount)));
            }
            result.append("</table>\n");
            result.append(format("<p>Amount owed is <em>%s</em></p>\n", usd(data.totalAmount)));
            result.append(format("<p>You earned <em>%d</em> credits</p>\n", data.totalVolumeCredits));
            return result.toString();
        }

        private String renderPlainText(StatementData data) {
            StringBuilder result = new StringBuilder(format("Statement for %s\n", data.customer));
            for (PerformanceExt perf : data.performances) {
                result.append(format(" %s: %s (%d seats)\n", playFor(perf).name, usd(perf.amount), perf.audience));
            }
            result.append(format("Amount owed is %s\n", usd(data.totalAmount)));
            result.append(format("You earned %d credits\n", data.totalVolumeCredits));
            return result.toString();
        }

        private String usd(double amount) {
            return currency.format(amount / 100.0);
        }

        public Play playFor(Performance aPerformance) {
            return plays.get(aPerformance.playID);
        }
    }
}
