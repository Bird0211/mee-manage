package com.mee.manage.service;

import org.jdmp.core.algorithm.classification.AbstractClassifier;
import org.jdmp.core.algorithm.classification.Classifier;
import org.jdmp.core.algorithm.classification.bayes.NaiveBayesClassifier;
import org.jdmp.core.dataset.ListDataSet;
import org.jdmp.core.sample.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation;
import org.ujmp.core.enums.ValueType;
import org.ujmp.core.util.MathUtil;

public class NaiveBayesTextClassifier extends AbstractClassifier {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(NaiveBayesTextClassifier.class);


    private MeeDensityEstimator[][] dists = null;

    private MeeDensityEstimator[] classDists = null;

    private int classCount = -1;

    public NaiveBayesTextClassifier() {
    }

    public NaiveBayesTextClassifier(String inputLabel) {
        super(inputLabel);
    }

    public Matrix predictOne(Matrix input) {
        input = input.toColumnVector(Calculation.Ret.LINK);
        final double[] probs = new double[classCount];
        final double[] logs = new double[classCount];

        ValueType vt = input.getValueType();
        logger.info(vt.name());

        for (int j = 0; j < classCount; j++) {
            logs[j] += Math.log(classDists[j].getProbability(1.0));
        }

//        logger.info("PreDictOne");
        // for all features
        for (int j = 0; j < input.getColumnCount(); j++) {
            // for all classes
            double probSum = 0;
            for (int i = 0; i < classCount; i++) {
//                double value = input.getAsDouble(0, j);
                String value = input.getAsString(0, j);

//                logger.info("PreDictValue = {}",value);
                double probability = dists[j][i].getProbability(value);
//                logger.info("PreDictValue = {} ; probability = {}",value,probability);

                probs[i] = probability;
                probSum += probability;
            }
            for (int i = 0; i < classCount; i++) {
                logs[i] += Math.log(probs[i] / probSum);
            }
        }

        final double[] finalProbs = MathUtil.logToProbs(logs);
        Matrix m = Matrix.Factory.linkToArray(finalProbs).transpose();
        return m;
    }

    public void reset() {
        dists = null;
        classDists = null;
    }

    public void trainAll(ListDataSet dataSet) {
        System.out.println("training started");
        int featureCount = (int) dataSet.get(0).getAsMatrix(getInputLabel()).getValueCount();
        boolean discrete = isDiscrete(dataSet);
        classCount = getClassCount(dataSet);

        this.dists = new MeeDensityEstimator[featureCount][classCount];
        this.classDists = new MeeDensityEstimator[classCount];

        for (int j = 0; j < classCount; j++) {
            classDists[j] = new MeeDensityEstimator();
            for (int i = 0; i < featureCount; i++) {
                if (discrete) {
                    dists[i][j] = new MeeDensityEstimator();
                } else {
                    dists[i][j] = new MeeDensityEstimator();
                }
            }
        }

        System.out.println("density estimators created");

        int count = 0;
        for (Sample s : dataSet) {
            final Matrix sampleInput = s.getAsMatrix(getInputLabel()).toColumnVector(Calculation.Ret.LINK);
            final Matrix sampleTarget = s.getAsMatrix(getTargetLabel()).toColumnVector(Calculation.Ret.LINK);
            final double weight = s.getWeight();


            for (int j = 0; j < classCount; j++) {
                double classValue = sampleTarget.getAsDouble(0, j);
                if (classValue == 0.0) {
                    classDists[j].addValue(0.0, weight);
                } else {
                    logger.info("ClassValue = {}",classValue);
                    classDists[j].addValue(1.0, weight);
                    for (int i = 0; i < sampleInput.getColumnCount(); i++) {
//                        double inputValue = sampleInput.getAsDouble(0, i);
                        String inputValue = sampleInput.getAsString(0,i);
                        logger.info("InputValue = {}",inputValue);
                        logger.info("Train = {}; weight = {}",inputValue,weight);
                        dists[i][j].addStrValue(inputValue, weight);
                    }
                }
            }

            count++;
            if (count % 10000 == 0) {
                System.out.println(count);
            }
        }

        System.out.println("training finished");
    }

    public Classifier emptyCopy() {
        NaiveBayesClassifier nb = new NaiveBayesClassifier();
        nb.setInputLabel(getInputLabel());
        nb.setTargetLabel(getTargetLabel());
        return nb;
    }
}
