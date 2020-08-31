package com.mee.manage.vo;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
// import com.recognition.software.jdeskew.ImageDeskew;

import net.sourceforge.tess4j.util.ImageHelper;

public class Tools  {

    // private final static double MINIMUM_DESKEW_THRESHOLD = 0.05d;


    static int index = 0;

    public static void te () {
        List<Long> result = Lists.newArrayList(null,null,null,null,null,null,null,null);
        result.stream().filter(Objects::nonNull).reduce(0L, Long::sum);  
    }

    public static void main(String[] args) {
        te();

//        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIS39+jnjLSkSL0ig/IJsjwHnZ9gagoWxDzaoTOhds01oymDQsBP+0hlGs6QKDFWk4BSWp0xpvt30weZhuz04TUCAwEAAQ==";

//        String password = "mvpWSjWQ2itv3KgYAZqSLV94WXe6l5sDoc5a5CTlTQBkRKSGf4yXy9ZuAohrzseVeGvZKpdB9VvdkojtAm0n1A==";
        try {
//            String publicKey = ConfigTools.getPublicKey(null);
//            String pwd = ConfigTools.encrypt("B75Sq*qn");
            String pwd = ConfigTools.encrypt("397SKa8j");

//            String pwd = ConfigTools.decrypt(password);
            System.out.printf(pwd);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        String word = "Descrip@on";
        String target = "description";

        String correct = EnWordChecker.getInstance().correct(word.toLowerCase());

        System.out.println("word = "+word + ",correct = "+correct);

        float similarity = SimilarityStrUtil.getSimilarityRatio(target.toLowerCase().trim(),word.toLowerCase().trim());
        System.out.println(similarity);

        */
    }



    /**
     * 匹配是否为数字
     * @param str 可能为中文，也可能是-19162431.1254，不使用BigDecimal的话，变成-1.91624311254E7
     * @return
     * @author yutao
     * @date 2016年11月14日下午7:41:22
     */
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isCorrect(String word,String target){
        if(word == null || StringUtils.isEmpty(word))
            return false;

        boolean flag = false;
        float similarity = SimilarityStrUtil.getSimilarityRatio(word.toLowerCase().trim(),target.toLowerCase().trim());
        if(similarity > 0.80) {
            flag = true;
        }

        return flag;
    }

    //对图片进行处理 - 提高识别度
    public static BufferedImage convertImage(BufferedImage image) throws Exception {
        //按指定宽高创建一个图像副本
//        image = ImageHelper.getSubImage(image, 0, 0, image.getWidth(), image.getHeight());
        //图像转换成灰度的简单方法 - 黑白处理
        image = ImageHelper.convertImageToGrayscale(image);
        //锐化
        image = ImageHelper.convertImageToBinary(image);
        //图像缩放 - 放大n倍图像
//        image = ImageHelper.getScaledInstance(image, image.getWidth() * 2, image.getHeight() * 2);

        //去除歪斜
//        image = rotateImage(image);

        //降噪
        image = ssn(image);

        //二值化
        image = ImageHelper.convertImageToBinary(image);

        return image;
    }

    /*
    private static BufferedImage rotateImage(BufferedImage textImg) {
        ImageDeskew id = new ImageDeskew(textImg);
        double imageSkewAngle = id.getSkewAngle(); // determine skew angle
        if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
            textImg = ImageHelper.rotateImage(textImg, -imageSkewAngle); // deskew image
        }
        return textImg;

    }
    */

    private static BufferedImage ssn(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] pix = new int[w * h];
        image.getRGB(0, 0, w, h, pix, 0, w);
        int[] newpix = snnFiltering(pix, w, h);
        image.setRGB(0, 0, w, h, newpix, 0, w);
        return image;
    }

    /**
     * 对称近邻均值滤波
     *
     * @param pix 像素矩阵数组
     * @param w   矩阵的宽
     * @param h   矩阵的高
     * @return 处理后的数组
     */
    private static int[] snnFiltering(int[] pix, int w, int h) {
        int[] newpix = new int[w * h];
        int n = 9;
        int i1, i2, sum;
        int[] temp1 = new int[n];
        int[] temp2 = new int[n / 2];
        ColorModel cm = ColorModel.getRGBdefault();
        int r = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x != 0 && x != w - 1 && y != 0 && y != h - 1) {
                    sum = 0;
                    temp1[0] = cm.getRed(pix[x - 1 + (y - 1) * w]);
                    temp1[1] = cm.getRed(pix[x + (y - 1) * w]);
                    temp1[2] = cm.getRed(pix[x + 1 + (y - 1) * w]);
                    temp1[3] = cm.getRed(pix[x - 1 + (y) * w]);
                    temp1[4] = cm.getRed(pix[x + (y) * w]);
                    temp1[5] = cm.getRed(pix[x + 1 + (y) * w]);
                    temp1[6] = cm.getRed(pix[x - 1 + (y + 1) * w]);
                    temp1[7] = cm.getRed(pix[x + (y + 1) * w]);
                    temp1[8] = cm.getRed(pix[x + 1 + (y + 1) * w]);
                    for (int k = 0; k < n / 2; k++) {
                        i1 = Math.abs(temp1[n / 2] - temp1[k]);
                        i2 = Math.abs(temp1[n / 2] - temp1[n - k - 1]);
                        temp2[k] = i1 < i2 ? temp1[k] : temp1[n - k - 1];  //选择最接近原像素值的一个邻近像素
                        sum = sum + temp2[k];
                    }
                    r = sum / (n / 2);
                    //System.out.println("pix:" + temp1[4] + "  r:" + r);
                    newpix[y * w + x] = 255 << 24 | r << 16 | r << 8 | r;
                } else {
                    newpix[y * w + x] = pix[y * w + x];
                }
            }
        }
        return newpix;
    }

}
