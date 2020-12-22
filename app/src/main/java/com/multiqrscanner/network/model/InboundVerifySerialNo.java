package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;


public class InboundVerifySerialNo {
    @SerializedName("serialno")
    private String serialno;

    @SerializedName("iditem")
    private String iditem;

    @SerializedName("date")
    private Long date;

    public InboundVerifySerialNo() {
    }

    public InboundVerifySerialNo(String serialno, Long date, String iditem) {
        this.serialno = serialno;
        this.iditem = iditem;
        this.date = date;
    }


    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getIditem() {
        return iditem;
    }

    public void setIditem(String iditem) {
        this.iditem = iditem;
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
        sb.append(", iditem='").append(iditem).append('\'');
        sb.append(", date=").append(date);
        sb.append('}');
        return sb.toString();
    }
}
