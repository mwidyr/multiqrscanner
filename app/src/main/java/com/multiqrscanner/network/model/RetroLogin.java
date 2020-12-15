package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroLogin {
    @SerializedName("ad_user_id")
    private String userId;
    @SerializedName("resultCode")
    private int resultCode;
    @SerializedName("warehouse")
    private String warehouse;
    @SerializedName("role")
    private String role;
    @SerializedName("menus")
    private List<Menu> menus;

    public RetroLogin() {
    }

    public RetroLogin(String id, int resultCode, String warehouse, String role, List<Menu> menus) {
        this.userId = id;
        this.resultCode = resultCode;
        this.warehouse = warehouse;
        this.role = role;
        this.menus = menus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroLogin{");
        sb.append("id='").append(userId).append('\'');
        sb.append(", resultCode=").append(resultCode);
        sb.append(", warehouse='").append(warehouse).append('\'');
        sb.append(", role='").append(role).append('\'');
        sb.append(", menus=").append(menus);
        sb.append('}');
        return sb.toString();
    }
}