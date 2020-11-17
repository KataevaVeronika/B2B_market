package com.pupptmstr.parsermodule.servise;

import java.io.*;
import java.util.*;

import com.pupptmstr.parsermodule.parser.PdfParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    @GetMapping("/parse")
    public String parse() throws IOException {

        return "Use POST request";
    }

    @PostMapping("/parse")
    public Object parse(
        @RequestParam("file") MultipartFile[] files) throws IOException {
        List<File> listOfFiles = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isEmpty()) {
                try {
                    byte[] bytes = files[i].getBytes();
                    BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(files[i].getName() + "-uploaded")));
                    stream.write(bytes);
                    stream.close();
                    File fileToParse = new File(files[i].getName());
                    listOfFiles.add(fileToParse);
                } catch (Exception e) {
                    return "Вам не удалось загрузить " + files[i].getName() + " => " + e.getMessage();
                }
            }
        }
        List<String> res = PdfParser.parse(listOfFiles);
        return res;
    }
}
