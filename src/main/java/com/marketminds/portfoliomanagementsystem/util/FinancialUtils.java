package com.marketminds.portfoliomanagementsystem.util;

import java.math.BigDecimal;
import java.math.MathContext;

public class FinancialUtils {

    // CAGR = ((Ending Value / Beginning Value) ^ (1 / Years)) - 1
    public static double calculateCAGR(double startValue, double endValue, double years) {
        if (startValue <= 0 || years <= 0) return 0;
        return (Math.pow((endValue / startValue), (1.0 / years)) - 1) * 100;
    }

    // Max Drawdown = (Peak - Trough) / Peak
    public static double calculateMaxDrawdown(double[] portfolioValues) {
        double maxDD = 0, peak = Double.NEGATIVE_INFINITY;
        for (double val : portfolioValues) {
            if (val > peak) peak = val;
            double dd = (peak - val) / peak;
            if (dd > maxDD) maxDD = dd;
        }
        return maxDD * 100;
    }

    // Annualized Volatility (Standard Deviation of Daily Returns * sqrt(252))
    public static double calculateVolatility(double[] dailyReturns) {
        double sum = 0, sumSq = 0;
        for (double r : dailyReturns) {
            sum += r;
            sumSq += r * r;
        }
        double mean = sum / dailyReturns.length;
        double variance = (sumSq / dailyReturns.length) - (mean * mean);
        return Math.sqrt(variance) * Math.sqrt(252) * 100;
    }
}