package com.pupptmstr.parsermodule.servise;

import java.io.*;
import java.util.*;

import com.pupptmstr.parsermodule.servise.parser.ItemGroup;
import com.pupptmstr.parsermodule.servise.parser.PdfParser;
import com.pupptmstr.parsermodule.servise.respmodels.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    @GetMapping("/parse")
    public String parse() throws IOException {
        return "Use POST requests to:"
               + "\n\t-'/parse/document/' with @RequestParam('files')"
               + "\n\t-'/parse/studbook/' with @RequestParam('file')";
    }

    @PostMapping("/parse/document")
    public ResponseEntity<ResponseModel> parseDocument(
        @RequestParam("files") MultipartFile[] files) throws IOException {
        Map<String, List<ItemGroup>> parsedFiles = new HashMap<>();
        List<String> errors = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            if (filename != null) {
                if (!file.isEmpty()) {
                    File fileToParse = new File("/tmp", filename);
                    try {
                        byte[] bytes = file.getBytes();
                        BufferedOutputStream stream =
                            new BufferedOutputStream(new FileOutputStream(fileToParse));
                        stream.write(bytes);
                        stream.close();
                        List<ItemGroup> parsedFile = PdfParser.parseDocument(fileToParse);
                        if (parsedFile.isEmpty()) {
                            errors.add(filename);
                        } else {
                            parsedFiles.put(filename, parsedFile);
                        }
                        if (fileToParse.delete()) {
                            System.out.println("INFO  : " + filename + " deleted");
                        } else {
                            System.out.println("ERROR : " + filename + " not deleted");
                        }
                    } catch (IOException e) {
                        errors.add(filename);
                        if (fileToParse.exists()) {
                            if (fileToParse.delete()) {
                                System.out.println("INFO  : " + filename + " deleted");
                            } else {
                                System.out.println("ERROR : " + filename + " not deleted");
                            }
                        }
                    }
                } else {
                    errors.add(filename);
                }
            }
        }
        List<String> errorsRes = new ArrayList<>();
        for (String errorFileName : errors) {
            if (!isEmptyOrBlankString(errorFileName)) {
                errorsRes.add(errorFileName);
            }
        }
        ResponseModel res = new ResponseModel(parsedFiles, errorsRes);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/parse/studbook")
    public ResponseEntity<String> parseStudentBook(
        @RequestParam("file") MultipartFile file
    ) {
        String text = null;
        String filename = file.getOriginalFilename();
        if (filename != null) {
            if (!file.isEmpty()) {
                File fileToParse = new File(filename);
                try {
                    byte[] bytes = file.getBytes();
                    BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(fileToParse));
                    stream.write(bytes);
                    stream.close();
                    String extension = getFileExtension(filename);

                    if ("pdf".equals(extension)) {
                        text = PdfParser.parseTextBook(fileToParse);
                    }

                    if (fileToParse.delete()) {
                        System.out.println("INFO  : " + filename + " deleted");
                    } else {
                        System.out.println("ERROR : " + filename + " not deleted");
                    }
                } catch (IOException e) {
                    if (fileToParse.exists()) {
                        if (fileToParse.delete()) {
                            System.out.println("INFO  : " + filename + " deleted");
                        } else {
                            System.out.println("ERROR : " + filename + " not deleted");
                        }
                    }
                }
            }
        }
        if (text == null || isEmptyOrBlankString(text)) {
            return new ResponseEntity<>("Can't read this type of file.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } else {
            return new ResponseEntity<>(text, HttpStatus.OK);
        }
    }

    private static String getFileExtension(String fileName) {
        return fileName.split("\\.")[fileName.split("\\.").length - 1];
    }

    private static boolean isEmptyOrBlankString(String str) {
        return (str.isEmpty() || str.isBlank());
    }
}

