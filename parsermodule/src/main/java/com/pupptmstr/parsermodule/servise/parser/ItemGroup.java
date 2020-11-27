package com.pupptmstr.parsermodule.servise.parser;

import com.google.gson.annotations.SerializedName;
import com.pupptmstr.parsermodule.servise.parser.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {
    @SerializedName("GroupName")
    public String groupName;
    @SerializedName("Items")
    public List<Item> items;

    public ItemGroup(String name) {
        groupName = name;
        items = new ArrayList<>();
    }
}
