package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.mee.manage.controller.OCRController;
import com.mee.manage.po.Products;
import com.mee.manage.service.IDataMiningService;
import com.mee.manage.service.IProductsService;
import com.mee.manage.vo.MatchingRequest;
import org.jdmp.core.algorithm.classification.bayes.NaiveBayesClassifier;
import org.jdmp.core.dataset.DataSet;
import org.jdmp.core.dataset.ListDataSet;
import org.jdmp.core.sample.DefaultSample;
import org.jdmp.core.sample.Sample;
import org.jdmp.core.variable.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ujmp.core.Matrix;
import org.ujmp.core.mapmatrix.MapMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static org.jdmp.core.sample.Sample.PREDICTED;

@Service
public class DataMiningServiceImpl implements IDataMiningService {

    private static final Logger logger = LoggerFactory.getLogger(IDataMiningService.class);


    @Autowired
    IProductsService productsService;

    @Override
    public String classification(MatchingRequest request) throws IOException {

        test();


        ListDataSet tainData = getTrainDataSet();

        ListDataSet testData = getTestDataSet(request);


        // create a classifier
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();

        // train the classifier using all data
        classifier.trainAll(tainData);

        // use the classifier to make predictions
        classifier.predictAll(testData);


        // get the results
        double accuracy = testData.getAccuracy();
        logger.info("Accuracy = {}",accuracy);

        ListIterator<Sample> list = testData.listIterator();
        while (list.hasNext()) {
            logger.info("List");
            Sample s = list.next();
            if (s.getAsMatrix(classifier.getTargetLabel()) != null) {
                logger.info("classifier.getTargetLabel() = {}",classifier.getTargetLabel());
            } else {
                logger.info("classifier.getTargetLabel() is null");
            }
            Matrix output = s.getAsMatrix(Variable.PREDICTED);
            if(output != null) {

                logger.info("MaxValue = {}",s.getRecognizedClass());
            }
        }

        return ""+accuracy;
    }


    private ListDataSet getTestDataSet(MatchingRequest request){

        if(request == null || request.getName() == null)
            return null;

        ListDataSet testData = DataSet.Factory.emptyDataSet();
        for (int i = 0; i < request.getName().length; i++) {

            String name = request.getName()[i];
            Sample sample = Sample.Factory.emptySample();

            sample.put("name", name);
            Matrix input = Matrix.Factory.linkToArray(new String[] { name }).transpose();
            sample.put(Variable.INPUT, input);

            sample.setId("mee-test-"+i);

            testData.add(sample);
        }
        testData.setLabel("Mee Products Test data set");
        testData.setDescription("Mee All Products Date Set");
        return testData;
    }

    private ListDataSet getTrainDataSet() {
        ListDataSet tainData = DataSet.Factory.emptyDataSet();
        tainData.addAll(productsService.getSampleProducts());

        tainData.setLabel("Mee Products data set");
//        dataSet.setMetaData(Sample.URL, "http://archive.ics.uci.edu/ml/datasets/Iris");
        tainData.setDescription("Mee All Products Date Set");
        return tainData;
    }


    private void test() {
        // load example data set
        ListDataSet dataSet = DataSet.Factory.IRIS();

// create a classifier
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();

// train the classifier using all data
        classifier.trainAll(dataSet);

// use the classifier to make predictions
        classifier.predictAll(dataSet);

        Matrix matrix = dataSet.get(0).getAsMatrix(PREDICTED);
        if(matrix != null) {
            double maxValue = matrix.getMaxValue();
            logger.info("MaxValue = {}",maxValue);

            double d = matrix.getAsDouble(0,0);
            logger.info("GetAsDouble = {}",d);


        }

// get the results
        double accuracy = dataSet.getAccuracy();

        logger.info("accuracy = {}",accuracy);

    }
}
