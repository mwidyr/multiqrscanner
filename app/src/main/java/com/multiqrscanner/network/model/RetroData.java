package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

public class RetroData {
    @SerializedName("user")
    private RetroUser user;
    @SerializedName("error_message")
    private String errorMessage;
    @SerializedName("status")
    private String status;
    @SerializedName("message")
    private String message;

    public RetroData() {
    }

    public RetroData(RetroUser user, String errorMessage, String status, String message) {
        this.user = user;
        this.errorMessage = errorMessage;
        this.status = status;
        this.message = message;
    }

    public RetroUser getUser() {
        return user;
    }

    public void setUser(RetroUser user) {
        this.user = user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroData{");
        sb.append("user=").append(user);
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }
}