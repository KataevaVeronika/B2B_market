package com.pupptmstr.parsermodule.parser;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        System.out.println(text);

        document.close();
        return text;
    }

    public static List<ItemGroup> parseDocument(File file) throws IOException {
        /* TODO("Проблемы:
             - Производитель один на несколько строк
             - Вертикально ориентированный документ
             - Перенос названий на новые страницы
             - Ошибки типа 'отсутствие toUnicode()' (возможное решение - использование OCR)
             - 2 листа на одной странице(ФЗУ11 - канал.pdf)")
         */

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
                    if (!text.equals("")) {
                        result.append(text).append(" | ");
                    }
                }
                result.append("\n").append("__--__--__").append("\n");
            }
        }
        document.close();
        return result.toString();
    }

    private static String clearText(String text) {
        StringBuilder clearText = new StringBuilder();
        boolean isShouldSkip = false;
        String[] splitText = text.split("__--__--__");
        clearText.append("\n__--__--__\n");
        for (String line : splitText) {
            String[] splitLineInline = line.split("\n");
            for (String lineInline : splitLineInline) {
                String currentLine = lineInline.strip().replace("\r", " ");
                if (!currentLine.contains("Лист")
                    && !currentLine.startsWith("Поз")
                    && !currentLine.startsWith("Наименование")
                    && !currentLine.contains("Проверил")
                    && !isShouldSkip) {
                    clearText.append(" ").append(currentLine);
                }
                if (isShouldSkip) {
                    isShouldSkip = false;
                }
                if (currentLine.contains("Лист") || currentLine.contains("Проверил")) {
                    isShouldSkip = true;
                }
            }
            clearText.append("\n__--__--__\n");
        }
        String result = clearText.toString().replace("\n__--__--__\n", "\n").replaceAll("( )+", " ");
        clearText = new StringBuilder();
        for (String line : result.split("\n")) {
            String currentLine = line.strip().replace("\r", " ");
            if (!currentLine.equals("") && !currentLine.equals(" ")) {
                clearText.append(currentLine).append("\n");
            }
        }
        result = clearText.toString();

        return result;
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
            String nextLine = null;
            Matcher nextLineMatcher = null;
            if (i != splitText.length - 1) {
                nextLine = splitText[i + 1];
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

    private static Item createItem(String line) {
        Pattern groupPattern =
            Pattern.compile(
                "(([0-9]+.)( \\|))(( [^|]+)( \\|))(( [^|]+)( \\|))?((.+)( \\|))?(( [A-Za-z\\u0410-\\u042f\\u0430-\\u044f]+)( \\|))?(( [\\u0430-\\u044f0-9. ]+)( \\|))(( [0-9.,/]+)( \\|))(( [0-9.,]+)( \\|))?(.+( \\|))?");
        Item res = new Item("", "", "", "");
        Matcher matcher = groupPattern.matcher(line);
        if (matcher.matches()) {
            try {
                res = new Item(matcher.group(5), matcher.group(11), matcher.group(20), matcher.group(17));
            } catch (IndexOutOfBoundsException e) {
                //res stays empty
            }
        }
        return res;
    }
}