package com.multiqrscanner.inventory.model;

import java.util.List;

public class InventoryData {
    private String id;
    private String inboundno;
    private String inbounddate;
    private String customer;
    private String warehouse;
    private List<InventoryDetail> items;

    public InventoryData() {
    }

    public InventoryData(String id, String inboundno, String inbounddate, String customer, String warehouse, List<InventoryDetail> items) {
        this.id = id;
        this.inboundno = inboundno;
        this.inbounddate = inbounddate;
        this.customer = customer;
        this.warehouse = warehouse;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInboundno() {
        return inboundno;
    }

    public void setInboundno(String inboundno) {
        this.inboundno = inboundno;
    }

    public String getInbounddate() {
        return inbounddate;
    }

    public void setInbounddate(String inbounddate) {
        this.inbounddate = inbounddate;
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

    public List<InventoryDetail> getItems() {
        return items;
    }

    public void setItems(List<InventoryDetail> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundData{");
        sb.append("id='").append(id).append('\'');
        sb.append(", inboundno='").append(inboundno).append('\'');
        sb.append(", inbounddate='").append(inbounddate).append('\'');
        sb.append(", customer='").append(customer).append('\'');
        sb.append(", warehouse='").append(warehouse).append('\'');
        sb.append(", items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
