package com.multiqrscanner.inventory.model;

public class InventoryDetail {
    private String lineNo, sku, serialNo, productName, qty, subkey, status;
    private Long inputDate;

    public InventoryDetail() {
    }

    public InventoryDetail(String lineNo, String sku, String serialNo, String productName, String qty, String subkey, String status, Long inputDate) {
        this.lineNo = lineNo;
        this.sku = sku;
        this.serialNo = serialNo;
        this.productName = productName;
        this.qty = qty;
        this.subkey = subkey;
        this.status = status;
        this.inputDate = inputDate;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getSubkey() {
        return subkey;
    }

    public void setSubkey(String subkey) {
        this.subkey = subkey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getInputDate() {
        return inputDate;
    }

    public void setInputDate(Long inputDate) {
        this.inputDate = inputDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundDetail{");
        sb.append("lineNo='").append(lineNo).append('\'');
        sb.append(", sku='").append(sku).append('\'');
        sb.append(", serialNo='").append(serialNo).append('\'');
        sb.append(", productName='").append(productName).append('\'');
        sb.append(", qty='").append(qty).append('\'');
        sb.append(", subkey='").append(subkey).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", inputDate='").append(inputDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
