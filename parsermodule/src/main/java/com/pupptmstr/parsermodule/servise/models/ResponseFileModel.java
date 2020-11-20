package com.pupptmstr.parsermodule.servise.models;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.pupptmstr.parsermodule.parser.ItemGroup;

public class ResponseFileModel {
    @SerializedName("FileName")
    String fileName;
    @SerializedName("ParsedDoc")
    List<ItemGroup> parsedDoc;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public List<ItemGroup> getParsedDoc() {
        return parsedDoc;
    }

    public void setParsedDoc(final List<ItemGroup> parsedDoc) {
        this.parsedDoc = parsedDoc;
    }

    public ResponseFileModel(String fileName, List<ItemGroup> parsedDoc) {
        this.fileName = fileName;
        this.parsedDoc = parsedDoc;
    }
}
