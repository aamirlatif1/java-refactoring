package com.uf.data;

import java.util.List;

public class StatementData {
    public String customer;
    public double totalAmount;
    public int totalVolumeCredits;
    public List<PerformanceExt> performances;

    public double totalAmount() {
        return performances.stream()
                .map(p -> p.amount)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public int totalVolumeCredit() {
        return performances.stream()
                .map(p -> p.volumeCredit)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
