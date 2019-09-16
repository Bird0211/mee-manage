package com.mee.manage.service;

import org.jdmp.core.algorithm.estimator.AbstractDensityEstimator;
import org.jdmp.core.algorithm.estimator.GeneralDensityEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MeeDensityEstimator extends AbstractDensityEstimator {

    private static final Logger logger = LoggerFactory.getLogger(MeeDensityEstimator.class);


    private final Map<Object, Double> counts;
    private double sumOfCounts;

    public MeeDensityEstimator() {
        counts = new HashMap<Object, Double>(2);
    }

    public void addStrValue(String value, double weight) {
        logger.info("addStrValue = {}",value);
        Double count = counts.get(value);
        if (count == null) {
            count = 0.0;
        }

        counts.put(value, count + weight);

        sumOfCounts += weight;
    }

    public void addValue(Object value, double weight) {
        Double count = counts.get(value);
        if (count == null) {
            count = 0.0;
        }

        counts.put(value, count + weight);

        sumOfCounts += weight;
    }

    public void addValue(final double value, final double weight) {
        addValue(Double.valueOf(value), weight);
    }

    public double getProbability(final double value) {
        return getProbability(Double.valueOf(value));
    }

    public double getProbability(final Object value) {
        if (sumOfCounts == 0.0) {
            return MINPROBABILITY;
        } else {
            Double count = counts.get(value);
            logger.info("Value = {} ; count = {}",value,count);
            if (count == null || count == 0.0) {
                return MINPROBABILITY;
            } else {
                double probability = count / sumOfCounts;
                return probability < MINPROBABILITY ? MINPROBABILITY : probability;
            }
        }
    }

    public void removeValue(final Object value, final double weight) {
        Double count = counts.get(value);
        if (count != null) {
            count -= weight;
            counts.put((String) value, count);
            sumOfCounts -= weight;
        }
    }

    public void removeValue(double value, double weight) {
        removeValue(Double.valueOf(value), weight);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MeeDensityEstimator");
        for (Object key : counts.keySet()) {
            sb.append(" " + key + ":" + counts.get(key));
        }
        return sb.toString();
    }

}
