package com.multiqrscanner.inventory.model;

import java.util.HashMap;

public class PutawayData {
    private PalletDetail palletDetail;
    private HashMap<String,InventoryDetail> productDetail;

    public PutawayData() {
    }

    public PutawayData(PalletDetail palletDetail, HashMap<String, InventoryDetail> productDetail) {
        this.palletDetail = palletDetail;
        this.productDetail = productDetail;
    }

    public PalletDetail getPalletDetail() {
        return palletDetail;
    }

    public void setPalletDetail(PalletDetail palletDetail) {
        this.palletDetail = palletDetail;
    }

    public HashMap<String, InventoryDetail> getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(HashMap<String, InventoryDetail> productDetail) {
        this.productDetail = productDetail;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PutawayData{");
        sb.append("palletDetail=").append(palletDetail);
        sb.append(", productDetail=").append(productDetail);
        sb.append('}');
        return sb.toString();
    }
}
