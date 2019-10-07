package com.mee.manage.vo;

import com.alibaba.fastjson.JSON;
import com.mee.manage.enums.InvoiceEnum;
import com.mee.manage.service.IOCRService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class TextOverlayVo {
    private static final Logger logger = LoggerFactory.getLogger(TextOverlayVo.class);

    List<LineVo> lines;

    boolean HasOverlay;

    String Message;

    List<WordsVo> allWords;

    InvoiceTypeVo invoiceType;

    Integer maxTop;

    public Integer getMaxTop(){
        if(maxTop != null && maxTop > 0)
            return maxTop;


        LineVo lastLine = lines == null ? null : lines.get(lines.size()-1);
        maxTop = lastLine.getMinTop() + lastLine.getMaxHeight();
        return maxTop;
    }


    public InvoiceVo getInVoice(){
        InvoiceVo invoice = new InvoiceVo();
        InvoiceTypeVo invoiceType = getInvoiceType(getAllWords());

        invoice.setInvoiceDate(getInvoceValue(invoiceType.getInvoiceDateName(),invoiceType.getDateLocation()));
        invoice.setInvoiceNo(getInvoceValue(invoiceType.getInvoiceNoName(),invoiceType.getNoLocation()));
        invoice.setProducts(getProducts());
        return invoice;
    }

    private InvoiceTypeVo getInvoiceType(List<WordsVo> words){
        if(invoiceType != null)
            return invoiceType;

        InvoiceEnum[] invoiceEnums = InvoiceEnum.values();
        InvoiceEnum keyInvoice = null;
        boolean flag = false;
        for(WordsVo word : words){
            for(InvoiceEnum invoice : invoiceEnums){
                if(Tools.isCorrect(word.getWordText(),invoice.getKeyWord())) {
                    flag = true;
                    keyInvoice = invoice;
                    break;
                }
            }

            if(flag)
                break;
        }
        InvoiceTypeVo invoiceType = null;
        if(keyInvoice != null) {
            invoiceType = new InvoiceTypeVo();
            invoiceType.setDescriptionName(keyInvoice.getDescriptionName());
            invoiceType.setEndLineName(keyInvoice.getEndLineName());
            invoiceType.setInvoiceDateName(keyInvoice.getDateName());
            invoiceType.setInvoiceNoName(keyInvoice.getNoName());
            invoiceType.setQuantityName(keyInvoice.getQuantityName());
            invoiceType.setUnitPriceName(keyInvoice.getUnitPriceName());
            invoiceType.setKeyWord(keyInvoice.getKeyWord());
            invoiceType.setTypeName(keyInvoice.name());
            invoiceType.setNoLocation(keyInvoice.getNoLocation());
            invoiceType.setDateLocation(keyInvoice.getDateLocation());
            invoiceType.setSkuName(keyInvoice.getSku());

            setInvoiceType(invoiceType);
        }
        return invoiceType;
    }

    public List<ProductsVo> getProducts(){
        WordsVo desWord = null;
        WordsVo qtyWord = null;
        WordsVo priceWord = null;
        WordsVo skuWord = null;

        List<WordsVo> allWords = getAllWords();
        if (allWords == null || allWords.isEmpty())
            return null;

        List<WordsVo> descript = new ArrayList<>();
        List<WordsVo> qty = new ArrayList<>();
        List<WordsVo> unitPrice = new ArrayList<>();
        List<WordsVo> sku = new ArrayList<>();
        InvoiceTypeVo invoiceType = getInvoiceType(allWords);
        if(invoiceType == null)
            return null;

        for(int i = 0; i < allWords.size(); i++){
            WordsVo word = allWords.get(i);
            if(word == null || StringUtils.isEmpty(word.getWordText()))
                continue;

            if(desWord != null || qtyWord != null || priceWord != null) {
                if(word.getWordText().toLowerCase().indexOf(invoiceType.getEndLineName()) > -1) {
                    break;
                }
            }

            if(!StringUtils.isEmpty(invoiceType.getDescriptionName())) {
                if (desWord == null) {
                    desWord = getKeyWord(i, invoiceType.getDescriptionName());
                    if (desWord != null) {

                        i = i + invoiceType.getDescriptionName().split(" ").length - 1;
                        continue;
                    }
                } else {
                    boolean flag = getAreaWord(desWord, word, descript);
                    if (flag)
                        continue;
                }
            }

            if(!StringUtils.isEmpty(invoiceType.getQuantityName())) {
                if (qtyWord == null) {
                    qtyWord = getKeyWord(i, invoiceType.getQuantityName());
                    if (qtyWord != null) {
                        i = i + invoiceType.getQuantityName().split(" ").length - 1;
                        continue;
                    }
                } else {
                    boolean flag = getAreaWord(qtyWord, word, qty);
                    if (flag)
                        continue;
                }
            }

            if(!StringUtils.isEmpty(invoiceType.getUnitPriceName())) {
                if (priceWord == null) {
                    priceWord = getKeyWord(i, invoiceType.getUnitPriceName());
                    if (priceWord != null) {
                        i = i + invoiceType.getUnitPriceName().split(" ").length - 1;
                        continue;
                    }

                } else {
                    boolean flag = getAreaWord(priceWord, word, unitPrice);
                    if (flag)
                        continue;
                }
            }

            if(!StringUtils.isEmpty(invoiceType.getSkuName())) {
                if (skuWord == null) {
                    skuWord = getKeyWord(i,invoiceType.getSkuName());
                    if(skuWord != null) {
                        i = i+ invoiceType.getSkuName().split(" ").length - 1;
                        continue;
                    }

                }else {
                    boolean flag = getAreaWord(skuWord,word,sku);
                    if(flag)
                        continue;
                }
            }

        }


        logger.info("Description = {} " , JSON.toJSONString(descript));

        logger.info("qty = {}",JSON.toJSONString(qty));

        logger.info("UnitPrice = {}",JSON.toJSONString(unitPrice));

        ItemVo items = new ItemVo();
        items.setSku(sku);
        items.setDescription(descript);
        items.setQty(qty);
        items.setUnitPrice(unitPrice);

        return mergeItem(items);


    }

    private List<WordsVo> getAllWords() {
        if(allWords != null && !allWords.isEmpty())
            return allWords;

        List<LineVo> lines = getLines();
        if(lines == null || lines.isEmpty())
            return null;

        if(lines == null || lines.isEmpty())
            return null;

        allWords = new ArrayList<>();
        for (LineVo line : lines){
            if(line == null)
                continue;

            List<WordsVo> rowWords = line.getWords();
            if(rowWords != null && !rowWords.isEmpty())
                allWords.addAll(rowWords);

        }
        Collections.sort(allWords);
        logger.info("Allworks = {}",JSON.toJSONString(allWords));
        return allWords;
    }

    private WordsVo getKeyWord(int i,String key) {
        if(key == null)
            return null;

        WordsVo keyWord = null;
        WordsVo word = allWords.get(i);
        String[] k = key.split(" ");
        if(k == null || k.length <= 0) {
            return null;
        }

        if (i > allWords.size() - k.length) {
            return null;
        }

        boolean isCorr = true;
        for (int j = 0; j < k.length; j++){
            boolean flag = Tools.isCorrect(allWords.get(i + j).getWordText(), k[j]);
            if(!flag) {
                isCorr = false;
                break;
            }
        }

        if (isCorr) {
            int[] leftNum = getWordLeft(i,k.length);

            keyWord = word.clone();

            keyWord.setLeft(leftNum[0]);
            if (leftNum[1] > 0)
                keyWord.setWidth(leftNum[1] - leftNum[0]);
            else
                keyWord.setWidth(-1);
            logger.info("getKey = {}",JSON.toJSONString(keyWord));
        }

        return keyWord;
    }

    private boolean getAreaWord(WordsVo keyWord,WordsVo word,List<WordsVo> allAreawords) {
        boolean flag = false;
        if(isAreaWord(word.getLeft(),word.getWidth(),keyWord.getLeft(),keyWord.getWidth())) {
            if(allAreawords.isEmpty())
                allAreawords.add(word);
            else {
                WordsVo lastWord = allAreawords.get(allAreawords.size() -1 );
                if(word.getLeft() > lastWord.getLeft() + lastWord.getWidth() &&
                        word.getTop() < lastWord.getTop() + lastWord.getHeight()) {
                    lastWord.setWidth(word.getLeft() + word.getWidth() - lastWord.getLeft());
                    lastWord.setWordText(lastWord.getWordText() + " " + word.getWordText());
                    if(lastWord.getHeight() < word.getHeight())
                        lastWord.setHeight(word.getHeight());
                }else {
                    allAreawords.add(word);
                }
            }
            flag = true;
        }
        return flag;
    }

    private boolean isAreaWord(int wordLeft,int wordWidth,int keyWordLeft,int keyWordWidth){
        boolean isArea = false;
        if(wordLeft >= keyWordLeft){
            if (keyWordWidth == -1)
                isArea = true;
            else if(keyWordWidth > 0 && wordLeft + wordWidth <= keyWordLeft + keyWordWidth)
                isArea = true;
        }

        return isArea;
    }

    private List<ProductsVo> mergeItem(ItemVo items){
        if(items == null )
            return null;

        List<ProductsVo> products = null;
        List<WordsVo> description = items.getDescription();
        List<WordsVo> qty = items.getQty();
        List<WordsVo> unitPrice = items.getUnitPrice();
        List<WordsVo> sku = items.getSku();

        if(description == null || description.isEmpty())
            return null;

        if(description != null && (qty == null || qty.isEmpty()) && (unitPrice == null || unitPrice.isEmpty())) {
            products = getReceiptProjects(description);
        }else {
            products = getInvoiceProjects(description,qty,unitPrice,sku);
        }

        return products;
    }

    private  List<ProductsVo> getInvoiceProjects(List<WordsVo> description,List<WordsVo> qty,List<WordsVo> unitPrice,List<WordsVo> sku) {
        List<ProductsVo> products = new ArrayList<>();

        for (int i = 0; i < description.size(); i++) {
            WordsVo des = description.get(i);
            if(des == null)
                continue;

            WordsVo lastDes = null;
            if(i > 0) {
                lastDes = description.get(i-1);
            }

            String desc = des.getWordText();
            String num = getNum(des,lastDes,qty);
            String price = getNum(des,lastDes,unitPrice);
            String s = null;
            if(sku != null && !sku.isEmpty()) {
                s = getNum(des,lastDes,sku);
            }

            logger.info("Des = {},num = {},Price = {}",desc,num,price);

            if (num == null && price == null) {
                if(products != null && !products.isEmpty()) {
                    ProductsVo lastProduct = products.get(products.size() - 1);
                    lastProduct.setContent(lastProduct.getContent()+ " " + desc);
                    logger.info("merge Product = {}",JSON.toJSONString(lastProduct));
                    continue;
                }
            }

            if(num == null) {
                num = "0";
            }

            if(price == null) {
                price = "0";
            }



            Double numInteger = 0.0;
            try {
                numInteger = Double.parseDouble(num);
            }catch (Exception ex){
                logger.error("num change error, = {}",num);

            }

            BigDecimal priceBD = BigDecimal.ZERO;
            try {
                priceBD = new BigDecimal(price);
            }catch (Exception ex) {
                logger.error("price change error, = {}",price);

            }
            ProductsVo product = new ProductsVo();
            product.setContent(desc);
            product.setNum(numInteger);
            product.setPrice(priceBD);
            product.setSku(s);
            products.add(product);
        }
        return products;
    }

    private List<ProductsVo> getReceiptProjects(List<WordsVo> items){
        List<String> lines = splitWords(items);
        if(lines == null || lines.isEmpty())
            return null;

        List<ProductsVo> products = new ArrayList<>();


        String description = null;
        String qty = null;
        String unitPrice = null;
        for(String line : lines) {
            if (description == null) {
                description = line;
            }else {

                String[] numprice = line.split(" ");
                if(numprice == null || numprice.length < 2){
                    description = null;
                    continue;
                }

                String[] np = numprice[0].split("\\*");
                if(np == null || np.length < 2){
                    description = null;
                    continue;
                }

                unitPrice = np[0];
                qty = np[1];

                Double numInteger = 0.0;
                try {
                    numInteger = Double.parseDouble(qty);
                }catch (Exception ex){
                    logger.error("num change error, = {}",qty);

                }

                BigDecimal priceBD = BigDecimal.ZERO;
                try {
                    if(unitPrice.indexOf("$") > -1)
                        unitPrice = unitPrice.replace("$","");
                    priceBD = new BigDecimal(unitPrice);
                }catch (Exception ex) {
                    logger.error("price change error, = {}",unitPrice);

                }

                ProductsVo product = new ProductsVo();
                product.setSku(null);
                product.setContent(description);
                product.setNum(numInteger);
                product.setPrice(priceBD);
                products.add(product);
                description = null;
            }

        }

        return products;

    }

    private String getNum(WordsVo word,WordsVo lastWord,List<WordsVo> qtys){
        if(word == null || qtys == null)
            return null;

        String num = null;
        WordsVo minWord = null;
        int minTop = 0;
        if(lastWord != null)
            minTop = lastWord.getTop() + lastWord.getHeight();

        for(WordsVo qty : qtys) {
            if(qty == null)
                continue;

            if(minTop <= qty.getTop() &&
                    qty.getTop() <= word.getTop() + word.getHeight()) {
                if(minWord == null) {
                    minWord = qty;
                }else {
                    int minDis = Math.abs(minWord.getTop() - word.getTop());
                    int dis = Math.abs(qty.getTop() - word.getTop());
                    if(minDis > dis) {
                        minWord = qty;
                    }
                }
            }
        }

        if(minWord != null)
            num = minWord.getWordText();

        return num;
    }

    private int[] getWordLeft(int i,int skip) {
        List<WordsVo> allWords = getAllWords();
        WordsVo word = allWords.get(i);
        int minLeft = 0;
        int maxLeft = -1;
        if (i > 0) {
            WordsVo beforeWord = allWords.get(i - 1);
            int lastLeft = beforeWord.getLeft() + beforeWord.getWidth();
            int left = word.getLeft();
            if (lastLeft < left)
                minLeft = lastLeft;
        }

        if (i < allWords.size() - skip) {
            WordsVo nextWord = allWords.get(i + skip);
            int nextLeft = nextWord.getLeft();
            int workLeft =  word.getLeft() + word.getWidth();
            if (workLeft <= nextLeft) {
                maxLeft = nextLeft;
            }
        }

        int[] leftNum = {minLeft,maxLeft};

        return leftNum;
    }

    private String getInvoceValue(String keyName,int location){
        List<WordsVo> words = getAllWords();
        if(words == null || words.size() <= 0 || keyName == null) {
            return null;
        }

        String value = null;
        WordsVo dateWord = null;
        List<WordsVo> dateList = new ArrayList<>();
        for (int i = 0; i < words.size();i ++) {
            if(dateWord == null) {
                dateWord = getKeyWord(i, keyName);
                if(dateWord != null) {
                    i = i + keyName.split(" ").length - 1;
                    logger.info(dateWord.getWordText());
                    continue;
                }
            }
            if(dateWord != null) {
                if(location == 1 && i < words.size() -1) {
                    value = words.get(i).getWordText();
                    break;
                }else if(location == 2){
                    boolean flag = getAreaWord(dateWord,words.get(i),dateList);
                    if(flag && dateList.size() > 1) {
                        value = dateList.get(0).getWordText();
                        break;
                    }
                }
            }
        }

        logger.info("KeyName:{}; Value = {}" ,keyName,value);
        return value;
    }


    private List<String> splitWords(List<WordsVo> descriptions){
        List<WordsVo> lines = new ArrayList<>();
        WordsVo lastWord = null;
        for (WordsVo word : descriptions) {
            if (lines == null || lines.isEmpty()) {
                lines.add(word);
            }else {
                if (word.getLeft() > (lastWord.getLeft() + lastWord.getWidth()) && word.getTop() < (lastWord.getTop() + lastWord.getHeight())) {
                    WordsVo lastLine = lines.get(lines.size()-1);
                    lastLine.setWidth(lastLine.getWidth() + word.getWidth());
                    lastLine.setHeight(word.getHeight() > lastLine.getHeight() ? word.getHeight() : lastLine.getHeight());
                    lastLine.setWordText(lastWord.getWordText() + " "+word.getWordText());
                    lastLine.setTop(word.getTop() < lastLine.getTop() ? word.getTop() : lastLine.getTop());
                    lines.set(lines.size() - 1, lastLine);
                } else {
                    lines.add(word);
                }
            }
            lastWord = word;
        }

        logger.info("Lines = {}",JSON.toJSONString(lines));
        return null;
    }

}
