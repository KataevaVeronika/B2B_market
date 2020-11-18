package com.pupptmstr.parsermodule.servise.models;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.pupptmstr.parsermodule.parser.ItemGroup;

public class ResponseModel {
    @SerializedName("SuccessFiles")
    List<ResponseFileModel> successFiles;
    @SerializedName("ErrorsIn")
    List<String> errors;


    public ResponseModel(Map<String, List<ItemGroup>> parsedFiles, List<String> errors) {
        this.errors = errors;
        successFiles = new ArrayList<>();
        for (Map.Entry<String, List<ItemGroup>> entry : parsedFiles.entrySet()) {
            successFiles.add(new ResponseFileModel(entry.getKey(), entry.getValue()));
        }
    }
}
