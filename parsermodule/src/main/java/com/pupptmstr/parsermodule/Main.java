package com.pupptmstr.parsermodule;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

//Redundant class. Will remove when start making service from this module
public class Main {
    public static void main(String[] args) {
        try {
            List<File> list = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("pdfs"))) {
                for (Path path : stream) {
                    if (!Files.isDirectory(path)) {
                        list.add(new File("pdfs/" + path.getFileName().toString()));
                    }
                }
            }

            List<String> listOfResults = PdfParser.parse(list);
            for (int i = 0; i < listOfResults.size(); i++) {
                File outputFile = new File("res/" + list.get(i).getName().replace(".pdf", "") + ".txt");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                outputFile.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, false));
                writer.write(listOfResults.get(i));
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}