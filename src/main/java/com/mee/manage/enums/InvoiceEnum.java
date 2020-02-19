package com.mee.manage.enums;

public enum InvoiceEnum {

    BETA("beta", "invoice date", 2, "invoice number", 2, "description",
            "quantity", "unit price", "total", null),

    MITOQ("mitoq", "date", 1, "account no.", 2, "description",
            "quantity", "price", "total", null),

    SAVAR("inclusiv", "invoice date", 2, "invoice number", 2, "description",
            "quantity", "unit price", "total", null),

    EGOMALL("egomall", "invoice date", 2, "invoice number", 2, "description",
            "quantity", "unit price", "total", null),

    LIVINGNATURE("living nature", "invoice date", 1, "invoice no:", 1, "description",
            "qty", "unit price", "courier", null),

    HUAYANG("hua", "invoice date:", 1, "invoice #:", 1, "description",
            "ot", "unit price", "GST", null),

    PARKERGO("parker&co", "due date", 1, "invoices no:", 1, "item",
            "unit", "unit price", "total", "sku"),

    HEALTHCARE("healthcare", "due date", 1, "invoices no:", 1, "item",
            "unit", "unit price", "total", "sku"),

    USANA("usana", "order date", 1, "order id", 1, "description",
            "qty", "price", "product total", "part code"),

    HH("H&H", "invoice date:", 2, "reference:", 2, "description",
            "quantity", "unit price", "subtotal", null),

    YOMI("Yomi","invoice date",2,"invoice number",2,"description (name, alias, mpn)",
            "qty.","unit price","subtotal",null),

    PAKnSAVE("PAKnSAVE","invoice date",1,"invoice number",1,"product description",
            "supplied\nquantity","unit price",null,"product id"),

    DEFAUTL("null", "invoice date", 1, "invoices no", 1, "description",
            "quantity", "unit price", "total", "sku"),

    ;


    String keyWord;

    String dateName;

    int dateLocation;  // 1:right; 2:down

    String noName;

    int noLocation;

    String descriptionName;

    String quantityName;

    String unitPriceName;

    String endLineName;

    String sku;

    InvoiceEnum(String keyWord, String dateName, int dateLocation, String noName, int noLocation, String descriptionName,
                String quantityName, String unitPriceName, String endLineName, String sku) {
        this.keyWord = keyWord;
        this.dateName = dateName;
        this.noName = noName;
        this.descriptionName = descriptionName;
        this.quantityName = quantityName;
        this.unitPriceName = unitPriceName;
        this.endLineName = endLineName;
        this.dateLocation = dateLocation;
        this.noLocation = noLocation;
        this.sku = sku;
    }

    public String getDateName() {
        return dateName;
    }

    public String getDescriptionName() {
        return descriptionName;
    }

    public String getEndLineName() {
        return endLineName;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getNoName() {
        return noName;
    }

    public String getQuantityName() {
        return quantityName;
    }

    public String getUnitPriceName() {
        return unitPriceName;
    }

    public int getDateLocation() {
        return dateLocation;
    }

    public int getNoLocation() {
        return noLocation;
    }

    public String getSku() {
        return sku;
    }
}
