package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

public class RetroInboundsVerifyResponse {
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("message")
    private String message;

    public RetroInboundsVerifyResponse() {
    }

    public RetroInboundsVerifyResponse(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroInboundsVerifyResponse{");
        sb.append("resultCode=").append(resultCode);
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
