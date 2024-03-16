package edu.sjsu.articlevisualisationbackend.controller;

import edu.sjsu.articlevisualisationbackend.service.CheckPdfSize;
import edu.sjsu.articlevisualisationbackend.service.CheckUploadFile;
import edu.sjsu.articlevisualisationbackend.service.PdfToText;
import edu.sjsu.articlevisualisationbackend.service.SavePdfToDisk;
import edu.sjsu.articlevisualisationbackend.service.exception.InvalidPdfLengthException;
import edu.sjsu.articlevisualisationbackend.service.exception.InvalidUploadFileException;
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
    private final CheckPdfSize checkPdfSize;
    private final CheckUploadFile checkUploadFile;

    @Autowired
    public FileUploadController(SavePdfToDisk savePdfToDisk,
                                PdfToText pdfToText,
                                CheckPdfSize checkPdfSize,
                                CheckUploadFile checkUploadFile) {
        this.savePdfToDisk = savePdfToDisk;
        this.pdfToText = pdfToText;
        this.checkPdfSize = checkPdfSize;
        this.checkUploadFile = checkUploadFile;
    }

    @PostMapping("/uploadPdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {

            this.checkUploadFile.checkUploadFile(file);

            this.savePdfToDisk.setOriginalFile(file);
            String filePath = this.savePdfToDisk.saveTempPdf();

            String fileText = this.pdfToText.pdf_to_text(filePath);

            this.checkPdfSize.check_pdf_size(fileText);

            return new ResponseEntity<>(fileText, HttpStatus.OK);

        } catch (InvalidUploadFileException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InvalidPdfLengthException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
        } catch (Exception e) {
            return new ResponseEntity<>("Could not upload the file: " + file.getOriginalFilename() + "!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
