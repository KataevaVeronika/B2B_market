package com.pupptmstr.parsermodule.service;

import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pupptmstr.parsermodule.service.parser.ItemGroup;
import com.pupptmstr.parsermodule.service.parser.PdfParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void testStudBook() throws Exception {
        File fileToRead = getFileFromResources("studbooks/inputsb.pdf");
        File expectedFile = getFileFromResources("studbooks/expectedsb.txt");
        String expectedData = getFileData(expectedFile);
        String res = PdfParser.parseTextBook(fileToRead);
        Assertions.assertEquals(expectedData, res);
    }

    @Test
    public void testDocument() throws IOException, URISyntaxException {
        File fileToRead = getFileFromResources("docs/inputDoc.pdf");
        File expectedFile = getFileFromResources("docs/expectedDoc.txt");
        String expectedData = getFileData(expectedFile).trim().replace("\n", "");
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        List<ItemGroup> res = PdfParser.parseDocument(fileToRead);
        Assertions.assertEquals(expectedData, gson.toJson(res));
    }

    private File getFileFromResources(String path) throws URISyntaxException {
        File res = null;
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource != null) {
            res = new File(resource.toURI());
            return res;
        }
        return res;
    }

    private static String getFileData(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder res = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            res.append(line).append("\n");
        }
        return res.toString();
    }
}
