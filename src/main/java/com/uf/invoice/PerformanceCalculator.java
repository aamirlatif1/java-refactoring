package com.uf.invoice;

import com.uf.data.Performance;
import com.uf.data.Play;

public abstract class PerformanceCalculator {
    protected Performance performance;
    protected Play play;

    public PerformanceCalculator(Performance performance, Play play) {
        this.performance = performance;
        this.play = play;
    }

    abstract public double amount();

    public int volumeCredit(){
        return Math.max(performance.audience - 30, 0);
    }

    public static PerformanceCalculator createCalculator(Performance performance, Play play){
        switch (play.type){
            case "tragedy":
                return new TragedyCalculator(performance, play);
            case "comedy":
                return new ComedyCalculator(performance, play);
            default:
                throw new IllegalArgumentException("unknown type: " + play.type);
        }
    }
}
