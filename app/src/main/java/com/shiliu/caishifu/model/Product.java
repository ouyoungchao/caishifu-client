package com.shiliu.caishifu.model;

import android.net.Uri;

import java.util.List;

/**
 * 商品
 */
public class Product {
    private String name;
    private float price;
    private int supply;
    private List<Uri> pictures;

    public Product(String name, float price, int supply, List<Uri> pictures) {
        this.name = name;
        this.price = price;
        this.supply = supply;
        this.pictures = pictures;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getSupply() {
        return supply;
    }

    public void setSupply(int supply) {
        this.supply = supply;
    }

    public List<Uri> getPictures() {
        return pictures;
    }

    public void setPictures(List<Uri> pictures) {
        this.pictures = pictures;
    }
}
