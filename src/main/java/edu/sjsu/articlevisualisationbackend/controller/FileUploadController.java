package edu.sjsu.articlevisualisationbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
* Controller for handling file uploads.
*/
@RestController
public class FileUploadController {

    @PostMapping("/uploadPdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        // Check if the file is not empty and is a PDF
        if (file.isEmpty() || !isPdf(file)) {
            return new ResponseEntity<>("Invalid file. Please upload a PDF file.", HttpStatus.BAD_REQUEST);
        }

        try {

            saveFile(file);

            return new ResponseEntity<>("File uploaded successfully: " + file.getOriginalFilename(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not upload the file: " + file.getOriginalFilename() + "!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isPdf(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }

    private void saveFile(MultipartFile file) {

    }
}
