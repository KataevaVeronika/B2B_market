package com.pupptmstr.parsermodule;

import java.io.*;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class PdfParser {

    private PdfParser() {
    }

    public static List<String> parse(List<File> files) throws IOException {
        /* TODO("Проблемы:
             - Производитель один на несколько строк
             - Вертикально ориентированный документ
             - Перенос названий на новые страницы
             - Ошибки типа 'отсутствие toUnicode()' (возможное решение - использование OCR)
             - 2 листа на одной странице(ФЗУ11 - канал.pdf)")
         */

        List<String> unformattedTexts = new ArrayList<>();
        List<String> formattedTexts = new ArrayList<>();


        for (File file : files) {
            unformattedTexts.add(getTextFromPDF(file));
        }

        for (String unformattedText : unformattedTexts) {
            formattedTexts.add(clearText(unformattedText));
        }

        return new ArrayList<>(formattedTexts);
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
        clearText.append("__--__--__\n");
        for (String line : splitText) {
            String[] splitLineInline = line.split("\n");
            for (String lineInline : splitLineInline) {
                String currentLine = lineInline.strip().replace("\r", " ");
                if (!currentLine.contains("Лист") && !currentLine.startsWith("Поз") && !currentLine.startsWith("Наименование") && !isShouldSkip) {
                    clearText.append(" ").append(currentLine);
                }
                if (isShouldSkip) {
                    isShouldSkip = false;
                }
                if (currentLine.contains("Лист")) {
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
}