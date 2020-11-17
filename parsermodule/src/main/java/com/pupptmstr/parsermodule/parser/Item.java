package com.pupptmstr.parsermodule.parser;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("Item-Model")
    String itemAndModel;
    @SerializedName("Manufacturer")
    String manufacturer;
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
