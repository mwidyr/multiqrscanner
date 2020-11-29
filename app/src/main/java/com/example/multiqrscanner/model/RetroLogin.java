package com.example.multiqrscanner.model;

import com.google.gson.annotations.SerializedName;

public class RetroLogin {
    @SerializedName("data")
    private RetroData data;

    public RetroLogin() {
    }

    public RetroLogin(RetroData data) {
        this.data = data;
    }

    public RetroData getData() {
        return data;
    }

    public void setData(RetroData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroLogin{");
        sb.append("data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
