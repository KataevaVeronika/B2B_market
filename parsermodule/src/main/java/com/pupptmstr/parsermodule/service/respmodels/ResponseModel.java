package com.pupptmstr.parsermodule.service.respmodels;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.pupptmstr.parsermodule.service.parser.ItemGroup;

public class ResponseModel {
    @SerializedName("SuccessFiles")
    List<ResponseFileModel> successFiles;
    @SerializedName("ErrorsIn")
    List<String> errors;

    public List<ResponseFileModel> getSuccessFiles() {
        return successFiles;
    }

    public void setSuccessFiles(final List<ResponseFileModel> successFiles) {
        this.successFiles = successFiles;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(final List<String> errors) {
        this.errors = errors;
    }

    public ResponseModel(Map<String, List<ItemGroup>> parsedFiles, List<String> errors) {
        this.errors = errors;
        successFiles = new ArrayList<>();
        for (Map.Entry<String, List<ItemGroup>> entry : parsedFiles.entrySet()) {
            successFiles.add(new ResponseFileModel(entry.getKey(), entry.getValue()));
        }
    }
}