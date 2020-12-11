package com.pupptmstr.parsermodule.service.parser;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class PdfParser {
    private PdfParser() {
    }

    public static String parseTextBook(File file) throws IOException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setSortByPosition(true);
        String text = pdfStripper.getText(document);
        document.close();
        StringBuilder res = new StringBuilder();
        String[] str = text.split("\n");
        for (String line : str) {
            String clearLine = line.strip().replace("\r", "");
            res.append(clearLine).append("\n");
        }
        return res.toString();
    }

    @SuppressWarnings({"AvoidEscapedUnicodeCharacters", "LogicConditionNeedOptimization"})
    private static String clearText(String text) {
        StringBuilder clearText = new StringBuilder();
        boolean isShouldSkip = false;
        String[] splitText = text.split("__--__--__");
        clearText.append("\n__--__--__\n");
        for (String line : splitText) {
            String[] splitLineInline = line.split("\n");
            for (String lineInline : splitLineInline) {
                String currentLine = lineInline.strip().replace("\r", " ");
                if (!currentLine.contains("\u041B\u0438\u0441\u0442")
                    && !currentLine.startsWith("\u041F\u043E\u0437")
                    && !currentLine.startsWith("\u041D\u0430\u0438\u043C\u0435\u043D\u043E\u0432\u0430\u043D\u0438\u0435")
                    && !currentLine.contains("\u041F\u0440\u043E\u0432\u0435\u0440\u0438\u043B")
                    && !isShouldSkip) {
                    clearText.append(" ").append(currentLine);
                }
                if (isShouldSkip) {
                    isShouldSkip = false;
                }
                if (currentLine.contains("\u041B\u0438\u0441\u0442") || currentLine.contains("\u041F\u0440\u043E\u0432\u0435\u0440\u0438\u043B")) {
                    isShouldSkip = true;
                }
            }
            clearText.append("\n__--__--__\n");
        }
        String result = clearText.toString().replace("\n__--__--__\n", "\n").replaceAll("( )+", " ");
        clearText = new StringBuilder();
        for (String line : result.split("\n")) {
            String currentLine = line.strip().replace("\r", " ");
            if (!StringUtils.equals(currentLine, "") && !StringUtils.equals(currentLine, " ")) {
                clearText.append(currentLine).append("\n");
            }
        }
        result = clearText.toString();

        return result;
    }

    public static List<ItemGroup> parseDocument(File file) throws IOException {
        String unformattedText;
        String formattedText;
        List<ItemGroup> parsedJson;

        unformattedText = getTextFromPDF(file);
        formattedText = clearText(unformattedText);
        parsedJson = parseJson(formattedText);

        return parsedJson;
    }

    private static String getTextFromPDF(File file) throws IOException {
        ObjectExtractor objectExtractor;
        StringBuilder result = new StringBuilder();
        PDDocument document = PDDocument.load(file);
        objectExtractor = new ObjectExtractor(document);
        List<Table> tables = new ArrayList<>();
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            Page page = objectExtractor.extract(i);
            SpreadsheetExtractionAlgorithm extractionAlgorithm = new SpreadsheetExtractionAlgorithm();
            List<Table> tableList = extractionAlgorithm.extract(page);
            if (tableList.size() != 0) {
                int numOfTable = 0;
                int j = 0;
                int max = 0;
                for (Table table : tableList) {
                    if (table.getColCount() * table.getRowCount() > max) {
                        max = table.getColCount() * table.getRowCount();
                        numOfTable = j;
                    }
                    j++;
                }
                tables.add(tableList.get(numOfTable));
            }
        }

        for (Table table : tables) {
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColCount(); j++) {
                    String text = table.getCell(i, j).getText();
                    if (!StringUtils.equals(text, "")) {
                        result.append(text).append(" | ");
                    }
                }
                result.append("\n").append("__--__--__").append("\n");
            }
        }
        document.close();
        return result.toString();
    }

    private static List<ItemGroup> parseJson(String formattedText) {
        Pattern partOfFirstCellPattern =
            Pattern.compile("(([0-9]+.?)( \\| ))([A-Za-z\\u0410-\\u042f\\u0430-\\u044f0-9()\\-=,./ ]+( \\|))+");
        List<ItemGroup> res = new ArrayList<>();
        ItemGroup itemGroup = new ItemGroup("");
        String[] splitText = formattedText.split("\n");
        for (int i = 0; i < splitText.length; i++) {
            String currentLine = splitText[i];
            Matcher currentMatcher = partOfFirstCellPattern.matcher(currentLine);
            Matcher nextLineMatcher = null;
            if (i != splitText.length - 1) {
                String nextLine = splitText[i + 1];
                nextLineMatcher = partOfFirstCellPattern.matcher(nextLine);
            }

            if (currentMatcher.matches()) {
                itemGroup.items.add(createItem(currentLine));
            } else {
                if (nextLineMatcher != null) {
                    if (nextLineMatcher.matches()) {
                        res.add(itemGroup);
                        itemGroup = new ItemGroup(currentLine);
                    }
                }
            }
        }

        return res;
    }

    @SuppressWarnings({"MagicNumber", "LineLengthExtended"})
    private static Item createItem(String line) {
        Pattern groupPattern =
            Pattern.compile(
                "(([0-9]+\\.?)( \\|))(( [^|]+)( \\|))(( [^|]+)( \\|))?((.+)( \\|))?(( [A-Za-z\\u0410-\\u042f\\u0430-\\u044f]+)( \\|))?(( [\\u0430-\\u044f0-9. ]+)( \\|))(( [0-9.,/]+)( \\|))(( [0-9.,]+)( \\|))?(.+( \\|))?");
        Item res = new Item("", "", "", "");
        Matcher matcher = groupPattern.matcher(line);
        if (matcher.matches()) {
            try {
                int itemAndModelGroupNum = 5;
                int manufacturerGroupNum = 11;
                int quantityGroupNum = 20;
                int measureGroupNum = 17;

                res = new Item(matcher.group(itemAndModelGroupNum),
                    matcher.group(manufacturerGroupNum),
                    matcher.group(quantityGroupNum),
                    matcher.group(measureGroupNum));
            } catch (IndexOutOfBoundsException e) {
                //res stays empty
            }
        }
        return res;
    }
}
