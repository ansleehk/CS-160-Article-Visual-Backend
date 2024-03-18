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


    public FileUploadController() {


    }

    @PostMapping(value = "/uploadPdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {

            CheckUploadFile checkUploadFile = new CheckUploadFile(file);
            checkUploadFile.check();

            SavePdfToDisk savePdfToDisk = new SavePdfToDisk();

            savePdfToDisk.setOriginalFile(file);
            String filePath = savePdfToDisk.saveTempPdf();

            PdfToText pdfToText = new PdfToText(filePath);

            final String pdfText = pdfToText.getPdfTextContent();

            CheckPdfSize checkPdfSize = new CheckPdfSize();

            checkPdfSize.check_pdf_size(pdfText);

            final ChatGptDiagramGenerator chatGptDiagramGenerator = new ChatGptDiagramGenerator(pdfText);

            final String mermaidCode = chatGptDiagramGenerator.generateMermaidCode();

            savePdfToDisk.removeTempPdf();


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
