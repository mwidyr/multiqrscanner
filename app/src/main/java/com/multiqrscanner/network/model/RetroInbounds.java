package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;
import com.multiqrscanner.inbound.model.InboundData;

import java.util.List;

public class RetroInbounds {
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("items")
    private List<InboundData> items;

    public RetroInbounds() {
    }

    public RetroInbounds(int resultCode, List<InboundData> items) {
        this.resultCode = resultCode;
        this.items = items;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int inboundNo) {
        this.resultCode = resultCode;
    }

    public List<InboundData> getInbounds() {
        return items;
    }

    public void setInbounds(List<InboundData> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroInbounds{");
        sb.append("resultCode='").append(resultCode).append('\'');
        sb.append(", items='").append(items).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
