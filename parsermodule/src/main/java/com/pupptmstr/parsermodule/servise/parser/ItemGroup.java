package com.pupptmstr.parsermodule.servise.parser;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {
    @SerializedName("GroupName")
    String groupName;
    @SerializedName("Items")
    List<Item> items;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(final List<Item> items) {
        this.items = items;
    }

    public ItemGroup(String name) {
        groupName = name;
        items = new ArrayList<>();
    }
}
