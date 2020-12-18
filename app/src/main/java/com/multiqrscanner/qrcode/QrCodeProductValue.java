package com.multiqrscanner.qrcode;

import com.google.gson.annotations.SerializedName;

public class QrCodeProductValue {
    @SerializedName("sku")
    private String sku;
    @SerializedName("serialNo")
    private Long serialNo;
    @SerializedName("subkey")
    private String subkey;
    @SerializedName("valid")
    private String valid;

    public QrCodeProductValue() {
    }

    public QrCodeProductValue(String sku, Long serialNo, String subkey,String valid) {
        this.sku = sku;
        this.serialNo = serialNo;
        this.subkey = subkey;
        this.valid = valid;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Long serialNo) {
        this.serialNo = serialNo;
    }

    public String getSubkey() {
        return subkey;
    }

    public void setSubkey(String subkey) {
        this.subkey = subkey;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QrCodeProductValue{");
        sb.append("sku='").append(sku).append('\'');
        sb.append(", serialNo=").append(serialNo);
        sb.append(", subkey='").append(subkey).append('\'');
        sb.append(", valid='").append(valid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
