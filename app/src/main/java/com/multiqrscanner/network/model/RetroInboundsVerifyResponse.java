package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroInboundsVerifyResponse {
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("message")
    private String message;
    @SerializedName("items")
    private List<InboundVerifySerialNo> items;

    public RetroInboundsVerifyResponse() {
    }

    public RetroInboundsVerifyResponse(int resultCode, String message, List<InboundVerifySerialNo> items) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = items;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<InboundVerifySerialNo> getItems() {
        return items;
    }

    public void setItems(List<InboundVerifySerialNo> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroInboundsVerifyResponse{");
        sb.append("resultCode=").append(resultCode);
        sb.append(", message='").append(message).append('\'');
        sb.append(", items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
