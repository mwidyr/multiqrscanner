package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;
import com.multiqrscanner.inbound.model.InboundDetail;

import java.util.List;

public class RetroInboundItem {
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("items")
    private List<InboundDetail> items;

    public RetroInboundItem() {
    }

    public RetroInboundItem(int resultCode, List<InboundDetail> items) {
        this.resultCode = resultCode;
        this.items = items;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int inboundNo) {
        this.resultCode = resultCode;
    }

    public List<InboundDetail> getInbounds() {
        return items;
    }

    public void setInbounds(List<InboundDetail> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroInboundItem{");
        sb.append("resultCode='").append(resultCode).append('\'');
        sb.append(", items='").append(items).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
