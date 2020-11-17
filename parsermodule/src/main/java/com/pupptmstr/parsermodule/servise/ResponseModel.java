package com.pupptmstr.parsermodule.servise;

import java.util.*;

import com.google.gson.annotations.SerializedName;
import com.pupptmstr.parsermodule.parser.ItemGroup;

public class ResponseModel {
    @SerializedName("Names")
    List<String> names;

    public ResponseModel(List<String> jsons) {
        names = jsons;
    }
}
