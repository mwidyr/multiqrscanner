package com.multiqrscanner.network.model;

import com.google.gson.annotations.SerializedName;

public class Menu {
    @SerializedName("menu")
    String menu;

    public Menu() {
    }

    public Menu(String menu) {
        this.menu = menu;
    }

    void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMenu() {
        return menu;
    }
}
