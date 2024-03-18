package edu.sjsu.articlevisualisationbackend.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Convert PDF to Text.
 */
public class PdfToText {

    private String pdfFilePath;
    private PDDocument pdfFile;

    public PdfToText(String pdfFilePath) throws IOException {
        this.pdfFilePath = pdfFilePath;
        this.openPdf();
    }

    private void openPdf() throws IOException {
        File pdfFile = new File(pdfFilePath);
        this.pdfFile = Loader.loadPDF(pdfFile);
    }

    public String getPdfTextContent() throws IOException {
        final PDFTextStripper pdfStripper = new PDFTextStripper();
        final String text = pdfStripper.getText(this.pdfFile);

        this.pdfFile.close();
        return(text);
    }
}
