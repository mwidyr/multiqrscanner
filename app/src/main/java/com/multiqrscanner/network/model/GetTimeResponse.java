package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

public class GetTimeResponse {
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("message")
    private String message;
    @SerializedName("date")
    private String date;

    public GetTimeResponse() {
    }

    public GetTimeResponse(int resultCode, String message, String date) {
        this.resultCode = resultCode;
        this.message = message;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetTimeResponse{");
        sb.append("resultCode=").append(resultCode);
        sb.append(", message='").append(message).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
