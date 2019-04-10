package com.mee.manage.service.impl;

import com.mee.manage.controller.ManageController;
import com.mee.manage.service.IOCRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
public class OCRSeviceImpl implements IOCRService {

    private static final Logger logger = LoggerFactory.getLogger(IOCRService.class);

    private byte[] graphDef;

    private List<String> labels;

    @Override
    public void loadTrainingData(String path) {
        graphDef = readAllBytesOrExit(Paths.get(path, "tensorflow_inception_graph.pb"));
        labels = readAllLinesOrExit(Paths.get(path, "imagenet_comp_graph_label_strings.txt"));
    }

    @Override
    public String imageRecognition(byte[] imageBytes) {

        try (Tensor image = Tensor.create(imageBytes)) {
            float[] labelProbabilities = executeInceptionGraph(graphDef, image);

            int bestLabelIdx = maxIndex(labelProbabilities);
            logger.info("BestLabelIndex: {}",bestLabelIdx);
            String result = labels.get(bestLabelIdx);
            float likely = labelProbabilities[bestLabelIdx] * 100;
            logger.info("Best Result = {} , Likely ({}%) ",result,likely);

            return result +"("+(float)(Math.round(likely*100))/100+"%)";
        }

    }


    private byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private List<String> readAllLinesOrExit(Path path) {
        try {
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Failed to read [" + path + "]: " + e.getMessage());
            System.exit(0);
        }
        return null;
    }

    private float[] executeInceptionGraph(byte[] graphDef, Tensor image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g);
                 Tensor result = s.runner().feed
                         ("DecodeJpeg/contents", image).fetch("softmax").run().get(0)) {
                final long[] rshape = result.shape();
                if (result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(
                            String.format(
                                    "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                    Arrays.toString(rshape)));
                }
                int nlabels = (int) rshape[1];
                float[][] floats = (float[][]) result.copyTo(new float[1][nlabels]);
                return floats[0];
            }
        }
    }

    private static int maxIndex(float[] probabilities) {
        int best = 0;
        for (int i = 1; i < probabilities.length; ++i) {
            if (probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }

}
