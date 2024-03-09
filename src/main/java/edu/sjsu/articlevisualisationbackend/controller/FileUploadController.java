package edu.sjsu.articlevisualisationbackend.controller;

import edu.sjsu.articlevisualisationbackend.service.PdfToText;
import edu.sjsu.articlevisualisationbackend.service.SavePdfToDisk;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final SavePdfToDisk savePdfToDisk;
    private final PdfToText pdfToText;

    @Autowired
    public FileUploadController(SavePdfToDisk savePdfToDisk, PdfToText pdfToText) {
        this.savePdfToDisk = savePdfToDisk;
        this.pdfToText = pdfToText;
    }

    @PostMapping("/uploadPdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        // Check if the file is not empty and is a PDF
        if (file.isEmpty() || !isPdf(file)) {
            return new ResponseEntity<>("Invalid file. Please upload a PDF file.", HttpStatus.BAD_REQUEST);
        }

        try {

            this.savePdfToDisk.setOriginalFile(file);
            String filePath = this.savePdfToDisk.saveTempPdf();

            String fileText = this.pdfToText.pdf_to_text(filePath);

            return new ResponseEntity<>(fileText, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not upload the file: " + file.getOriginalFilename() + "!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isPdf(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }
}
