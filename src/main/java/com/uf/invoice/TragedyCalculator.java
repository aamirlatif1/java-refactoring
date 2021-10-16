package com.uf.invoice;

import com.uf.data.Performance;
import com.uf.data.Play;

public class TragedyCalculator extends PerformanceCalculator {
    public TragedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }

    @Override
    public double amount() {
        double result = 40000.0;
        if (performance.audience > 30) {
            result += 1000 * (performance.audience - 30);
        }
        return result;
    }
}
