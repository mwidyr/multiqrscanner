package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroInboundVerifyRequest {
    @SerializedName("id")
    private String id;
    @SerializedName("ad_user_id")
    private String userID;
    @SerializedName("verif")
    private String verif;
    @SerializedName("serialNos")
    private List<InboundVerifySerialNo> serialno;

    public RetroInboundVerifyRequest() {
    }

    public RetroInboundVerifyRequest(String id, String userID, String verif, List<InboundVerifySerialNo> serialno) {
        this.id = id;
        this.userID = userID;
        this.verif = verif;
        this.serialno = serialno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getVerif() {
        return verif;
    }

    public void setVerif(String verif) {
        this.verif = verif;
    }

    public List<InboundVerifySerialNo> getSerialno() {
        return serialno;
    }

    public void setSerialno(List<InboundVerifySerialNo> serialno) {
        this.serialno = serialno;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroInboundVerifyRequest{");
        sb.append("id='").append(id).append('\'');
        sb.append(", userID='").append(userID).append('\'');
        sb.append(", verif='").append(verif).append('\'');
        sb.append(", serialno=").append(serialno);
        sb.append('}');
        return sb.toString();
    }
}
