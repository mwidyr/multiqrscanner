package com.multiqrscanner.outbound.model;

public class OutboundDetail {
    private String lineNo, sku, serialNo, productName, qty, subkey, status, idItem;
    private Long inputDate;

    public OutboundDetail() {
    }

    public OutboundDetail(String lineNo, String sku, String serialNo, String productName, String qty, String subkey, String status, Long inputDate, String idItem) {
        this.lineNo = lineNo;
        this.sku = sku;
        this.serialNo = serialNo;
        this.productName = productName;
        this.qty = qty;
        this.subkey = subkey;
        this.status = status;
        this.inputDate = inputDate;
        this.idItem = idItem;
    }

    public String getIdItem() {
        return idItem;
    }

    public void setIdItem(String idItem) {
        this.idItem = idItem;
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
        sb.append(", idItem='").append(idItem).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
