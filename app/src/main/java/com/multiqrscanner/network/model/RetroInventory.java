package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;
import com.multiqrscanner.inventory.model.InventoryData;

import java.util.List;

public class RetroInventory {
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("items")
    private List<InventoryData> items;

    public RetroInventory() {
    }

    public RetroInventory(int resultCode, List<InventoryData> items) {
        this.resultCode = resultCode;
        this.items = items;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int inboundNo) {
        this.resultCode = resultCode;
    }

    public List<InventoryData> getInbounds() {
        return items;
    }

    public void setInbounds(List<InventoryData> items) {
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
