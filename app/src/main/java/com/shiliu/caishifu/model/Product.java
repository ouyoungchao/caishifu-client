package com.shiliu.caishifu.model;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.List;

/**
 * 商品
 */
public class Product extends SugarRecord implements Serializable {
    private String name;
    private float price;
    private int supply;
    private List<String> pictures;

    public Product() {
    }

    public Product(String name, float price, int supply, List<String> pictures) {
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

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", supply=" + supply +
                ", pictures=" + pictures +
                '}';
    }
}
