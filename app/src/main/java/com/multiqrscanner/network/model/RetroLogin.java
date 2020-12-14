package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetroLogin {
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

    public RetroLogin(int resultCode, String warehouse, String role, List<Menu> menus) {
        this.resultCode = resultCode;
        this.warehouse = warehouse;
        this.role = role;
        this.menus = menus;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public String getRole() {
        return role;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setData(int resultCode, String warehouse, String role, List<Menu> menus) {
        this.resultCode = resultCode;
        this.warehouse = warehouse;
        this.role = role;
        this.menus = menus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetroLogin{");
        sb.append("resultCode='").append(resultCode).append('\'');
        sb.append(",warehouse='").append(warehouse).append('\'');
        sb.append(",role='").append(role).append('\'');
        sb.append(",menus='").append(menus).append('\'');
        sb.append('}');
        return sb.toString();
    }
}