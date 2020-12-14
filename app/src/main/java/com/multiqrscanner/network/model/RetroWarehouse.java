package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

public class RetroWarehouse {
    @SerializedName("warehouse")
    private String warehouse;

    public RetroWarehouse() {
    }

    public RetroWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroWarehouse{");
        sb.append("warehouse='").append(warehouse).append('\'');
        sb.append('}');
        return sb.toString();
    }
}