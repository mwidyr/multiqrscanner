package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

public class RetroInboundId {
    @SerializedName("id")
    private String id;

    public RetroInboundId() {
    }

    public RetroInboundId(String warehouse) {
        this.id = id;
    }

    public String getWarehouse() {
        return id;
    }

    public void setWarehouse(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroInboundId{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}