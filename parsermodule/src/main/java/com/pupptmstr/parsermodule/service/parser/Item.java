package com.pupptmstr.parsermodule.service.parser;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("Item-Model")
    public String itemAndModel;
    @SerializedName("Manufacturer")
    public String manufacturer;
    @SerializedName("Quantity")
    public String quantity;
    @SerializedName("Measure")
    public String measure;

    public Item (String itemAndModel, String manufacturer, String quantity, String measure) {
        this.itemAndModel = itemAndModel;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.measure = measure;
    }
}
