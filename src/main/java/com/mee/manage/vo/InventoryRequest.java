package com.mee.manage.vo;

import lombok.Data;

import java.util.List;

@Data
public class InventoryRequest {

    String invoiceDate;

    String invoiceNo;

    List<InvoiceProduct> products;

    Integer purchaser;

}
