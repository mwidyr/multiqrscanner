package com.example.multiqrscanner.qrcode;

import com.google.gson.annotations.SerializedName;

public class QrCodeBarcodeSimpleWrapper {
    @SerializedName("qrValue")
    private String qrValue;
    @SerializedName("count")
    private String count;

    public QrCodeBarcodeSimpleWrapper() {
    }

    public QrCodeBarcodeSimpleWrapper(String qrValue, String count) {
        this.qrValue = qrValue;
        this.count = count;
    }

    public String getQrValue() {
        return qrValue;
    }

    public void setQrValue(String qrValue) {
        this.qrValue = qrValue;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QrCodeSimpleWrapper{");
        sb.append("qrValue='").append(qrValue).append('\'');
        sb.append(", count='").append(count).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
