package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

public class RetroUser {
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    public RetroUser() {
    }

    public RetroUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroUser{");
        sb.append("username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append('}');
        return sb.toString();
    }
}