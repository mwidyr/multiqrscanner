package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroInboundsDetail {
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("items")
    private List<InboundItemDetail> items;

    public RetroInboundsDetail() {
    }

    public RetroInboundsDetail(int resultCode, List<InboundItemDetail> items) {
        this.resultCode = resultCode;
        this.items = items;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public List<InboundItemDetail> getItems() {
        return items;
    }

    public void setItems(List<InboundItemDetail> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroInboundsDetail{");
        sb.append("resultCode=").append(resultCode);
        sb.append(", items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
