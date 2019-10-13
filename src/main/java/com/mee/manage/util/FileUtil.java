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

        TextOverlayVo mergeText = textVos.get(0);
        if (textVos.size() > 1) {
            for (int i = 1; i < textVos.size(); i++) {
                TextOverlayVo textVo = textVos.get(i);
                List<WordsVo> allWords = textVo.getAllWords();
                if (allWords != null) {
                    allWords.forEach((word) -> {
                        word.setTop(word.getTop() + mergeText.getMaxTop());
                    });
                    mergeText.getAllWords().addAll(allWords);
                }
            }
        }

        return mergeText;
    }

    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
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
