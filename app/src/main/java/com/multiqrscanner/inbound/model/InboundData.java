package com.multiqrscanner.inbound.model;

import java.util.List;

public class InboundData {
    private String inboundno;
    private String inbounddate;
    private String customer;
    private String warehouse;
    private List<InboundDetail> items;

    public InboundData() {
    }

    public InboundData(String inboundNo, String inboundDate, String customer, String warehouse, List<InboundDetail> items) {
        this.inboundno = inboundNo;
        this.inbounddate = inboundDate;
        this.customer = customer;
        this.warehouse = warehouse;
        this.items = items;
    }

    public String getInboundNo() {
        return inboundno;
    }

    public void setInboundNo(String inboundNo) {
        this.inboundno = inboundNo;
    }

    public String getInboundDate() {
        return inbounddate;
    }

    public void setInboundDate(String inboundDate) {
        this.inbounddate = inboundDate;
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
        return items;
    }

    public void setInboundDetailList(List<InboundDetail> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundData{");
        sb.append("inboundNo='").append(inboundno).append('\'');
        sb.append(", inboundDate='").append(inbounddate).append('\'');
        sb.append(", customer='").append(customer).append('\'');
        sb.append(", warehouse='").append(warehouse).append('\'');
        sb.append(", items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
