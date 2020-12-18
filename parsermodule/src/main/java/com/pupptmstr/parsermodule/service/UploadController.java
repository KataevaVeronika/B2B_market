package com.pupptmstr.parsermodule.service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.pupptmstr.parsermodule.service.parser.ItemGroup;
import com.pupptmstr.parsermodule.service.parser.PdfParser;
import com.pupptmstr.parsermodule.service.respmodels.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UploadController.class);

    @GetMapping("/parse")
    public String parse() {
        return "Use POST requests to:"
               + "\n\t-'/parse/document/' with @RequestParam('files')"
               + "\n\t-'/parse/studbook/' with @RequestParam('file')";
    }

    @PostMapping("/parse/document")
    public ResponseEntity<ResponseModel> parseDocument(@RequestParam("files") MultipartFile[] files) {
        Map<String, List<ItemGroup>> parsedFiles = new HashMap<>();
        List<String> errors = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            if (filename != null) {
                if (!file.isEmpty()) {
                    File fileToParse = new File("/tmp", filename);
                    try {
                        downloadFile(file, fileToParse);
                        parseDoc(parsedFiles, errors, fileToParse, filename);
                    } catch (IOException e) {
                        errors.add(filename);
                        if (fileToParse.exists()) {
                            if (fileToParse.delete()) {
                                log.info(filename + " deleted");
                            } else {
                                log.error(filename + " not deleted");
                            }
                        }
                    }
                } else {
                    errors.add(filename);
                }
            }
        }
        List<String> errorsRes = errors.stream()
                                       .filter(errorFileName -> !errorFileName.isBlank())
                                       .collect(Collectors.toList());

        ResponseModel res = new ResponseModel(parsedFiles, errorsRes);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/parse/studbook")
    public ResponseEntity<String> parseStudentBook(
        @RequestParam("file") MultipartFile file
    ) {
        String filename = file.getOriginalFilename();
        if (filename == null || file.isEmpty()) {
            return new ResponseEntity<>("Can't read this type of file.", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } else {
            String text = null;
            File fileToParse = new File(filename);
            try {
                downloadFile(file, fileToParse);
                String extension = getFileExtension(filename);

                if ("pdf".equals(extension)) {
                    text = PdfParser.parseTextBook(fileToParse);
                }

                if (fileToParse.delete()) {
                    log.info(filename + " deleted");
                } else {
                    log.error(filename + " not deleted");
                }
            } catch (IOException e) {
                if (fileToParse.exists()) {
                    if (fileToParse.delete()) {
                        log.info(filename + " deleted");
                    } else {
                        log.error(filename + " not deleted");
                    }
                }
            }
            return new ResponseEntity<>(text, HttpStatus.OK);
        }
    }

    private static void downloadFile(MultipartFile fileToRead, File fileToWrite) throws IOException {
        byte[] bytes = fileToRead.getBytes();
        BufferedOutputStream stream =
            new BufferedOutputStream(new FileOutputStream(fileToWrite));
        stream.write(bytes);
        stream.close();
    }

    private void parseDoc(Map<String, List<ItemGroup>> parsedFiles, List<String> errors, File fileToParse, String filename) throws IOException {
        List<ItemGroup> parsedFile = PdfParser.parseDocument(fileToParse);
        if (parsedFile.isEmpty()) {
            errors.add(filename);
        } else {
            parsedFiles.put(filename, parsedFile);
        }
        if (fileToParse.delete()) {
            log.info(filename + " deleted");
        } else {
            log.error(filename + " not deleted");
        }
    }

    private static String getFileExtension(String fileName) {
        return fileName.split("\\.")[fileName.split("\\.").length - 1];
    }
}

