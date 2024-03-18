package edu.sjsu.articlevisualisationbackend.controller;

import edu.sjsu.articlevisualisationbackend.service.*;
import edu.sjsu.articlevisualisationbackend.service.exception.InvalidPdfLengthException;
import edu.sjsu.articlevisualisationbackend.service.exception.InvalidUploadFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class FileUploadController {
    private final SavePdfToDisk savePdfToDisk;
    private final CheckPdfSize checkPdfSize;
    private final ChatGptDiagramGeneratorService chatGptDiagramGeneratorService;

    @Autowired
    public FileUploadController(SavePdfToDisk savePdfToDisk,
                                CheckPdfSize checkPdfSize,
                                ChatGptDiagramGeneratorService chatGptDiagramGeneratorservice) {
        this.savePdfToDisk = savePdfToDisk;
        this.checkPdfSize = checkPdfSize;
        this.chatGptDiagramGeneratorService = chatGptDiagramGeneratorservice;
    }

    @PostMapping(value = "/uploadPdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {

            CheckUploadFile checkUploadFile = new CheckUploadFile(file);
            checkUploadFile.check();

            this.savePdfToDisk.setOriginalFile(file);
            String filePath = this.savePdfToDisk.saveTempPdf();

            PdfToText pdfToText = new PdfToText(filePath);

            final String pdfText = pdfToText.getPdfTextContent();

            this.checkPdfSize.check_pdf_size(pdfText);

            this.chatGptDiagramGeneratorService.setPdfText(pdfText);
            final String mermaidCode = this.chatGptDiagramGeneratorService.generateMermaidCode();


            return new ResponseEntity<>(mermaidCode, HttpStatus.OK);

        } catch (InvalidUploadFileException e){

            return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
        } catch (InvalidPdfLengthException e){

            return new ResponseEntity<String>(e.toString(), HttpStatus.PAYLOAD_TOO_LARGE);
        } catch (Exception e) {

            return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
