package com.mee.manage.util;

import com.mee.manage.vo.TextOverlayVo;
import com.mee.manage.vo.WordsVo;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class FileUtil {

    public static List<String> files2Base64Imgs(MultipartFile[] files) throws IOException {
        if (files == null || files.length <= 0) {
            return null;
        }

        List<String> base64Imgs = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null)
                continue;

            base64Imgs.add(Base64.getEncoder().encodeToString(file.getBytes()));
        }
        return base64Imgs;
    }

    public static List<BufferedImage> imgFiles2BufferedImg(MultipartFile[] files) throws IOException {
        if (files == null || files.length <= 0) {
            return null;
        }

        List<BufferedImage> bufferedImages = new ArrayList<>();
        for (MultipartFile file : files) {
            InputStream inputStream = file.getInputStream();
            BufferedImage textImage = ImageIO.read(inputStream);
            bufferedImages.add(textImage);
        }
        return bufferedImages;
    }

    public static List<BufferedImage> files2BufferedImg(MultipartFile[] files) throws IOException {
        if (files == null || files.length <= 0) {
            return null;
        }

        List<BufferedImage> bufferedImages = new ArrayList<>();
        for (MultipartFile file : files) {
            InputStream inputStream = file.getInputStream();
            if (file.getContentType().indexOf("pdf") >= 0) {
                List<BufferedImage> images = PDFUtil.readPDF(inputStream);
                bufferedImages.addAll(images);
            } else {
                BufferedImage textImage = ImageIO.read(inputStream);
                bufferedImages.add(textImage);
            }
        }
        return bufferedImages;
    }

    public static List<String> files2Base64PdfImgs(MultipartFile[] files) throws IOException {
        List<BufferedImage> imgs = files2BufferedImg(files);
        List<String> base64Imgs = null;
        if (imgs != null) {
            base64Imgs = new ArrayList<>();
            for (BufferedImage img : imgs) {
                // bufferImage->base64
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", outputStream);
                String base64Img = Base64.getEncoder().encodeToString(outputStream.toByteArray());
                base64Imgs.add(base64Img);
            }
        }
        return base64Imgs;
    }

    public static TextOverlayVo mergeTextOver(List<TextOverlayVo> textVos) {
        if (textVos == null || textVos.isEmpty())
            return null;

        TextOverlayVo mergeText = new TextOverlayVo(textVos.get(0).getAllWords());
        if (textVos.size() > 1) {
            for (int i = 1; i < textVos.size(); i++) {
                TextOverlayVo textVo = textVos.get(i);

                List<WordsVo> allWords = textVo.getAllWords();
                if (allWords != null) {
                    allWords.forEach((word) -> {
                        word.setTop(word.getTop() + mergeText.getMaxTop());
                    });

                    mergeText.getAllWords().addAll(allWords);
                    mergeText.sort();
                    mergeText.refreshMasTop();
                }
            }
        }

        return mergeText;
    }


    /**
     * @Description:图片拼接 （注意：必须两张图片长宽一致哦）
     * @param type  横向拼接， 2 纵向拼接
     */
    public static BufferedImage mergeImage(List<BufferedImage> images, int type) {

        if(images == null || images.size() <= 0)
            return null;

        if (images.size() == 1)
            return images.get(0);

        int[][] ImageArrays = new int[images.size()][];
        for (int i = 0; i < images.size(); i++) {
            int width = images.get(i).getWidth();
            int height = images.get(i).getHeight();
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images.get(i).getRGB(0, 0, width, height, ImageArrays[i], 0, width);
        }

        int newHeight = 0;
        int newWidth = 0;
        for (int i = 0; i < images.size(); i++) {
            // 横向
            if (type == 1) {
                newHeight = newHeight > images.get(i).getHeight() ? newHeight : images.get(i).getHeight();
                newWidth += images.get(i).getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > images.get(i).getWidth() ? newWidth : images.get(i).getWidth();
                newHeight += images.get(i).getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {
            return null;
        }
        if (type == 2 && newHeight < 1) {
            return null;
        }

        // 生成新图片
        BufferedImage ImageNew = null;
        try {
            ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int width_i = 0;
            for (int i = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                if (type == 1) {
                    ImageNew.setRGB(width_i, 0, image.getWidth(), newHeight, ImageArrays[i], 0,
                            image.getWidth());
                    width_i += image.getWidth();
                } else if (type == 2) {
                    ImageNew.setRGB(0, height_i, newWidth, image.getHeight(), ImageArrays[i], 0, newWidth);
                    height_i += image.getHeight();
                }
            }

            String imageName = "/data/myimage.jpg";

            ImageIO.write(ImageNew, "JPEG", new File(imageName));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ImageNew;
    }

    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file == null || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }

    public static BufferedImage mergeBufferedImage(List<BufferedImage> bufferedImages) {
        if(bufferedImages == null || bufferedImages.size() <= 0)
            return null;

        if(bufferedImages.size() == 1)
            return bufferedImages.get(0);


        return mergeImage(bufferedImages,2);
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
