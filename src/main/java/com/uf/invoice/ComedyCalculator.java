package com.uf.invoice;

import com.uf.data.Performance;
import com.uf.data.Play;

public class ComedyCalculator extends PerformanceCalculator {
    public ComedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }

    @Override
    public double amount() {
        double result = 30000.0;
        if (performance.audience > 20) {
            result += 10000 + 500 * (performance.audience - 20);
        }
        result += 300 * performance.audience;
        return result;
    }

    @Override
    public int volumeCredit() {
        return super.volumeCredit() + performance.audience / 5;
    }
}
