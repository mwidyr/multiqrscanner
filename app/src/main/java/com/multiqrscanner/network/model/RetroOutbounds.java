package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;
import com.multiqrscanner.outbound.model.OutboundData;

import java.util.List;

public class RetroOutbounds {
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("items")
    private List<OutboundData> items;

    public RetroOutbounds() {
    }

    public RetroOutbounds(int resultCode, List<OutboundData> items) {
        this.resultCode = resultCode;
        this.items = items;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int inboundNo) {
        this.resultCode = resultCode;
    }

    public List<OutboundData> getInbounds() {
        return items;
    }

    public void setInbounds(List<OutboundData> items) {
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
