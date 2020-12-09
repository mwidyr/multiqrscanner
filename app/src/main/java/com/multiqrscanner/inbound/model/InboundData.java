package com.multiqrscanner.inbound.model;

import java.util.List;

public class InboundData {
    private String inboundNo;
    private String inboundDate;
    private String customer;
    private String warehouse;
    private List<InboundDetail> inboundDetailList;

    public InboundData() {
    }

    public InboundData(String inboundNo, String inboundDate, String customer, String warehouse, List<InboundDetail> inboundDetailList) {
        this.inboundNo = inboundNo;
        this.inboundDate = inboundDate;
        this.customer = customer;
        this.warehouse = warehouse;
        this.inboundDetailList = inboundDetailList;
    }

    public String getInboundNo() {
        return inboundNo;
    }

    public void setInboundNo(String inboundNo) {
        this.inboundNo = inboundNo;
    }

    public String getInboundDate() {
        return inboundDate;
    }

    public void setInboundDate(String inboundDate) {
        this.inboundDate = inboundDate;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public List<InboundDetail> getInboundDetailList() {
        return inboundDetailList;
    }

    public void setInboundDetailList(List<InboundDetail> inboundDetailList) {
        this.inboundDetailList = inboundDetailList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundData{");
        sb.append("inboundNo='").append(inboundNo).append('\'');
        sb.append(", inboundDate='").append(inboundDate).append('\'');
        sb.append(", customer='").append(customer).append('\'');
        sb.append(", warehouse='").append(warehouse).append('\'');
        sb.append(", inboundDetailList=").append(inboundDetailList);
        sb.append('}');
        return sb.toString();
    }
}
