package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;


public class InboundVerifySerialNo {
    @SerializedName("serialno")
    private String serialno;

    @SerializedName("date")
    private Long date;

    public InboundVerifySerialNo() {
    }

    public InboundVerifySerialNo(String serialno, Long date) {
        this.serialno = serialno;
        this.date = date;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundVerifySerialNo{");
        sb.append("serialno='").append(serialno).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
