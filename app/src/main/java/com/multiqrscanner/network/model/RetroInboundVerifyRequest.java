package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroInboundVerifyRequest {
    @SerializedName("id")
    private String id;
    @SerializedName("userid")
    private String userID;
    @SerializedName("serialno")
    private List<String> serialno;

    public RetroInboundVerifyRequest() {
    }

    public RetroInboundVerifyRequest(String id, String userID, List<String> serialno) {
        this.id = id;
        this.userID = userID;
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

    public List<String> getSerialno() {
        return serialno;
    }

    public void setSerialno(List<String> serialno) {
        this.serialno = serialno;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroInboundVerifyRequest{");
        sb.append("id='").append(id).append('\'');
        sb.append(", userID='").append(userID).append('\'');
        sb.append(", serialno=").append(serialno);
        sb.append('}');
        return sb.toString();
    }
}
