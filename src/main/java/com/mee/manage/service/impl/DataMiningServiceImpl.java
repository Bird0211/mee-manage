package com.mee.manage.service.impl;

import com.alibaba.fastjson.JSON;
import com.mee.manage.controller.OCRController;
import com.mee.manage.po.Products;
import com.mee.manage.service.IDataMiningService;
import com.mee.manage.service.IProductsService;
import com.mee.manage.service.NaiveBayesTextClassifier;
import com.mee.manage.util.NaiveBayes;
import com.mee.manage.util.NaiveBayesKnowledgeBase;
import com.mee.manage.util.StrUtil;
import com.mee.manage.vo.MatchingRequest;
import com.mee.manage.vo.MeeProductVo;
import org.jdmp.core.algorithm.classification.KNNClassifier;
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
import org.ujmp.core.enums.ValueType;
import org.ujmp.core.mapmatrix.MapMatrix;

import java.io.IOException;
import java.util.*;

import static org.jdmp.core.sample.Sample.PREDICTED;

@Service
public class DataMiningServiceImpl implements IDataMiningService {

    private static final Logger logger = LoggerFactory.getLogger(IDataMiningService.class);


    @Autowired
    IProductsService productsService;

    @Override
    public List<String> classification(MatchingRequest request) throws IOException {
        List<MeeProductVo> meeProductVos = productsService.getMeeProducts();

//        test();
//        predicted(request,meeProductVos);
        List<String> result = naiveBayes(meeProductVos,request.getName());
        return result;
    }

    private String predicted(MatchingRequest request,List<MeeProductVo> meeProductVos) {
        ListDataSet tainData = getTrainDataSet(meeProductVos);
        ListDataSet testData = getTestDataSet(request);

        // create a classifier
        NaiveBayesTextClassifier classifier = new NaiveBayesTextClassifier();

        // train the classifier using all data
        classifier.trainAll(tainData);

        // use the classifier to make predictions
        classifier.predictAll(testData);

        String inputLabel =  classifier.getInputLabel();
        logger.info("InputLabel = {}" ,inputLabel);

        // get the results
        double accuracy = testData.getAccuracy();
        logger.info("Accuracy = {}",accuracy);

        Set<String> setCode = productsService.getSetCode(meeProductVos);
        List<String> codes = new ArrayList<>(setCode);


        ListIterator<Sample> list = testData.listIterator();
        while (list.hasNext()) {
            Sample s = list.next();
            int recognizedClass = s.getRecognizedClass();
            Matrix output = s.getAsMatrix(Variable.PREDICTED);
            if(output != null) {
                showInfo(output);
                logger.info("RecognizedClass = {}", recognizedClass);
                if(recognizedClass > -1)
                    logger.info("Result: {}",codes.get(recognizedClass));
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
            Matrix input = Matrix.Factory.linkToArray(new String[] {name}).transpose();
            sample.put(Variable.INPUT, input);
            sample.setLabel(name);

            sample.setId("mee-test-"+i);

            testData.add(sample);
        }
        testData.setLabel("Mee Products Test data set");
        testData.setDescription("Mee All Products Date Set");
        return testData;
    }

    private ListDataSet getTrainDataSet(List<MeeProductVo> meeProductVos) {
        ListDataSet tainData = DataSet.Factory.emptyDataSet();
        tainData.addAll(productsService.getSampleProducts(meeProductVos));

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

        String inputLabel =  classifier.getInputLabel();
        logger.info("InputLabel = {}" ,inputLabel);

        ListIterator<Sample> list = dataSet.listIterator();
        while (list.hasNext()) {
            Sample s = list.next();
            logger.info("weight = {}",s.getWeight());
            if (s.getAsMatrix(classifier.getTargetLabel()) != null) {
                logger.info("classifier.getTargetLabel() = {}",classifier.getTargetLabel());
            } else {
                logger.info("classifier.getTargetLabel() is null");
            }
            Matrix output = s.getAsMatrix(Variable.PREDICTED);
            if(output != null) {
                showInfo(output);
                logger.info("RecognizedClass = {}", s.getRecognizedClass());
            }

        }

// get the results
        double accuracy = dataSet.getAccuracy();

        logger.info("accuracy = {}",accuracy);

    }

    private void showInfo(Matrix matrix) {
        double maxValue = matrix.getMaxValue();
        logger.info("MaxValue = {}",maxValue);

        long columnCount = matrix.getColumnCount();
        long rowCount = matrix.getRowCount();

//        logger.info("Col = {} ; Row = {}",columnCount,rowCount);

        double max = 0;
        for (long c = 0; c < columnCount;c++ ) {
            for (long r = 0; r < rowCount; r++) {
                double result = matrix.getAsDouble(c,r);
//                logger.info("{},{} = {}",c,r,result);
                if(result > max) {
                    max = result;
//                    logger.info("Max = {}",max);
                }

            }
        }
    }


    private List<String> naiveBayes(List<MeeProductVo> meeProductVos,String[] names) {
        Map<String, String[]> trainingExamples = new HashMap<>();
        for (MeeProductVo product : meeProductVos) {
            trainingExamples.put(product.getCode(),new String[]{product.getName()});
        }

        NaiveBayes nb = new NaiveBayes();//训练分类器
        nb.setChisquareCriticalValue(6.63); //假设检验中的假定值为0.01
        nb.train(trainingExamples);

        //get trained classifier
        NaiveBayesKnowledgeBase knowledgeBase = nb.getKnowledgeBase();

        //Test classifier
        nb = new NaiveBayes(knowledgeBase);

        List<String> result = new ArrayList<>();
        for(String name : names) {
            String output = nb.predict(name);
            logger.info("The sentense {} was classified as {}", name, output);
            result.add(output);

        }
        return result;

    }
}
