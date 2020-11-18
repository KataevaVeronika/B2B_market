package com.pupptmstr.parsermodule.servise.models;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.pupptmstr.parsermodule.parser.ItemGroup;

public class ResponseFileModel {
    @SerializedName("FileName")
    String fileName;
    @SerializedName("ParsedDoc")
    List<ItemGroup> parsedDoc;

    public ResponseFileModel(String fileName, List<ItemGroup> parsedDoc) {
        this.fileName = fileName;
        this.parsedDoc = parsedDoc;
    }
}
