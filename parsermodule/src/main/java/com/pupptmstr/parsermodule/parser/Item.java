package com.pupptmstr.parsermodule.parser;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("Item-Model")
    String itemAndModel;
    @SerializedName("Manufacturer")
    String manufacturer;

    public String getItemAndModel() {
        return itemAndModel;
    }

    public void setItemAndModel(final String itemAndModel) {
        this.itemAndModel = itemAndModel;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(final String quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(final String measure) {
        this.measure = measure;
    }

    @SerializedName("Quantity")
    String quantity;
    @SerializedName("Measure")
    String measure;

    public Item (String itemAndModel, String manufacturer, String quantity, String measure) {
        this.itemAndModel = itemAndModel;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.measure = measure;
    }
}
