package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InboundVerifySerialNo {
    @SerializedName("serialno")
    private String serialno;

    public InboundVerifySerialNo() {
    }

    public InboundVerifySerialNo(String serialno) {
        this.serialno = serialno;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InboundVerifySerialNo{");
        sb.append("serialno='").append(serialno).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
