package com.pupptmstr.parsermodule.service.respmodels;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.pupptmstr.parsermodule.service.parser.ItemGroup;

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
