package com.mee.manage.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import com.mee.manage.enums.InvoiceEnum;
import com.mee.manage.util.DateUtil;
import com.mee.manage.util.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class TextOverlayVo {
    private static final Logger logger = LoggerFactory.getLogger(TextOverlayVo.class);

    List<LineVo> lines;

    boolean HasOverlay;

    String Message;

    List<WordsVo> allWords;

    InvoiceTypeVo invoiceType;

    double maxTop;

    public TextOverlayVo(){

    }

    public TextOverlayVo(List<WordsVo> allWords) {
        if(allWords != null && allWords.size() > 0) {
            this.allWords = allWords;
            // sort();
        }
    }

    public void sort(){
        Collections.sort(this.allWords);
    }

    public double getMaxTop() {
        if (maxTop > 0)
            return maxTop;


        refreshMasTop();
        return maxTop;
    }

    public void refreshMasTop() {
        LineVo lastLine = lines == null ? null : lines.get(lines.size()-1);
        if (lastLine != null) {
            maxTop = lastLine.getMinTop() + lastLine.getMaxHeight();
        } else {
            if (allWords != null && allWords.size() > 0) {
                for (WordsVo word : allWords) {
                    if (word != null && word.getTop() > maxTop)
                        maxTop = word.getTop();
                }
            }
        }
    }


    public InvoiceVo getInVoice(){
        InvoiceVo invoice = new InvoiceVo();
        InvoiceTypeVo invoiceType = getInvoiceType(getAllWords());
        if (invoiceType != null) {
            logger.info("Invoice TypeName = {}",invoiceType.getTypeName());
            invoice.setInvoiceDate(DateUtil.stringToDateMatchForm(getInvoceValue(invoiceType.getInvoiceDateName(), invoiceType.getDateLocation())));
            invoice.setInvoiceNo(getInvoceValue(invoiceType.getInvoiceNoName(), invoiceType.getNoLocation()));
        }
        invoice.setProducts(getProducts());
        return invoice;
    }


    public InvoiceTypeVo getInvoiceType() {
        return getInvoiceType(allWords);
    }

    private InvoiceTypeVo getInvoiceType(List<WordsVo> words){
        if(invoiceType != null)
            return invoiceType;

        InvoiceEnum[] invoiceEnums = InvoiceEnum.values();
        InvoiceEnum keyInvoice = null;
        boolean flag = false;
        for(int i = 0 ; i < words.size(); i++){
            for(InvoiceEnum invoice : invoiceEnums){
                String[] keys = invoice.getKeyWord().split(" ");
                for(int v = 0; v < keys.length ; v++) {
                    String key = keys[v];
                    flag = Tools.isCorrect(words.get(i+v).getWordText(),key);
                    if(!flag) {
                        break;
                    }
                }

                if(!flag) {
                    continue;
                }

                if(flag) {
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

    public List<ProductsVo> getProducts() {
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
                if(invoiceType.getEndLineName() != null &&
                        word.getWordText().toLowerCase().indexOf(invoiceType.getEndLineName()) > -1) {
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
                        logger.info("QtyWord = {}", qtyWord);
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

    public List<WordsVo> getAllWords() {
        if (this.allWords != null && this.allWords.size() > 0) {
            return this.allWords;
        }

        List<LineVo> lines = getLines();
        if(lines == null || lines.isEmpty())
            return null;

        this.allWords = new ArrayList<>();
        for (LineVo line : lines){
            if(line == null)
                continue;

            List<WordsVo> rowWords = line.getWords();
            if(rowWords != null && !rowWords.isEmpty())
                allWords.addAll(rowWords);

        }
        // Collections.sort(allWords);
        logger.info("Allworks = {}",JSON.toJSONString(allWords));
        return allWords;
    }

    private WordsVo getKeyWord(int i,String key) {
        if(key == null)
            return null;

        WordsVo keyWord = null;
        WordsVo word = allWords.get(i);
        String[] k = null;

        int location = 1;
        if(key.indexOf(" ") > 0) {
            k = key.split(" ");
        } else if(key.indexOf("\n") > 0) {
            k = key.split("\n");
            location = 2;
        } else {
            k = new String[1];
            k[0] = key;
        }
        if(k == null || k.length <= 0) {
            return null;
        }

        if (i > allWords.size() - k.length) {
            return null;
        }

        boolean isCorr = true;
        for (int j = 0; j < k.length; j++) {
            WordsVo nextValue = getNextValue(i,j,location);
            if(nextValue == null) {
                isCorr = false;
                break;
            }

            if(location == 2)
                logger.info("Next Key : {} | Next Word : {}" ,k[j],nextValue.WordText);
            boolean flag = Tools.isCorrect(nextValue.getWordText(), k[j]);
            if(!flag) {
                isCorr = false;
                break;
            }
        }

        if(!isCorr) {
            isCorr = Tools.isCorrect(word.getWordText(), key.trim());
        }

        if (isCorr) {
            double[] leftNum = getWordLeft(i, location == 2 ? 1: k.length);

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

    private boolean isAreaWord(double wordLeft, double wordWidth, double keyWordLeft, double keyWordWidth) {
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
        double minTop = 0;
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
                    double minDis = Math.abs(minWord.getTop() - word.getTop());
                    double dis = Math.abs(qty.getTop() - word.getTop());
                    if(minDis > dis) {
                        minWord = qty;
                    }
                }
            }
        }

        if(minWord != null)
            num = StrUtil.getNumber(minWord.getWordText());

        return num;
    }

    private double[] getWordLeft(int i, int skip) {
        List<WordsVo> allWords = getAllWords();
        WordsVo word = allWords.get(i);
        logger.info("getWordLeft = {}",word);
        double minLeft = 0;
        double maxLeft = -1;
        if (i > 0) {
            WordsVo beforeWord = allWords.get(i - 1);
            double lastLeft = beforeWord.getLeft() + beforeWord.getWidth();
            double left = word.getLeft();
            if (lastLeft < left)
                minLeft = lastLeft;
        }

        if (i < allWords.size() - skip) {
            WordsVo nextWord = allWords.get(i + skip);
            double nextLeft = nextWord.getLeft();
            double workLeft = word.getLeft() + word.getWidth();
            if (workLeft <= nextLeft) {
                maxLeft = nextLeft;
            }
        }

        double[] leftNum = {minLeft, maxLeft};

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

    private WordsVo getNextValue(int i,int skip,int location){
        List<WordsVo> words = getAllWords();
        if(words == null || words.size() <= 0) {
            return null;
        }

        if(skip == 0) {
            return words.get(i);
        }

        WordsVo word = null;

        if(location == 1 && i < words.size() - skip -1) {
            word = words.get(i + skip);
        }else if(location == 2){
            double minLeft = 0;
            double maxRight = words.get(i).getLeft()+words.get(i).getWidth();
            double minTop = words.get(i).getTop() + words.get(i).getTop();

            if(i > 0) {
                minLeft = words.get(i-1).getLeft() + words.get(i-1).getWidth();
                if (minLeft > words.get(i).getLeft()) {
                    minLeft = words.get(i).getLeft();
                }
            }

            if(i < words.size()-1) {
                maxRight = words.get(i+1).getLeft();
                if(maxRight < words.get(i).getLeft() + words.get(i).getWidth()) {
                    maxRight = words.get(i).getLeft() + words.get(i).getWidth();
                }
            }

            List<WordsVo> areaWords = getAreaWord(minLeft,minTop,maxRight,0,skip);
            if(areaWords != null && areaWords.size() > 0) {
                logger.info("Next Words = {}",areaWords);
                word = areaWords.get(skip - 1);
            }
        }

        return word;
    }

    private List<WordsVo> getAreaWord(double minLeft,double minTop,double maxRight,double maxTop,int number){
        List<WordsVo> words = getAllWords();
        if(words == null || words.size() <= 0) {
            return null;
        }

        Stream<WordsVo> streamWords = words.stream();
        streamWords = streamWords.filter(x-> x.getLeft() >= minLeft && x.getTop() >= minTop && x.getLeft()+x.getWidth() <= maxRight);
        if(maxTop > 0) {
            streamWords = streamWords.filter(x -> x.getTop() < maxTop);
        }

        if(number > 0){
            streamWords = streamWords.sorted().limit(number);
        }
        return streamWords.collect(Collectors.toList());

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
