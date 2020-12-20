package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;


public class InboundVerifySKU {
    @SerializedName("sku")
    private String sku;

    @SerializedName("qty")
    private String qty;

    @SerializedName("date")
    private Long date;

    public InboundVerifySKU() {
    }

    public InboundVerifySKU(String sku, String qty, Long date) {
        this.sku = sku;
        this.qty = qty;
        this.date = date;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundVerifySKU{");
        sb.append("sku='").append(sku).append('\'');
        sb.append(", qty='").append(qty).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
