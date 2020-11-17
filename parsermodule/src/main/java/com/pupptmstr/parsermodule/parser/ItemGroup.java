package com.pupptmstr.parsermodule.parser;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {
    @SerializedName("GroupName")
    String groupName;
    @SerializedName("Items")
    List<Item> items;

    public ItemGroup(String name) {
        groupName = name;
        items = new ArrayList<>();
    }
}
