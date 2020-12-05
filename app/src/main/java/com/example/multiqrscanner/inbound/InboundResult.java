package com.example.multiqrscanner.inbound;

public class InboundResult {
    private String productName;
    private String serialNo;
    private String status;

    public InboundResult() {
    }

    public InboundResult(String productName, String serialNo, String status) {
        this.productName = productName;
        this.serialNo = serialNo;
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundResult{");
        sb.append("productName='").append(productName).append('\'');
        sb.append(", serialNo='").append(serialNo).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
