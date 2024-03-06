package edu.sjsu.articlevisualisationbackend.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/**
 * Service _ Convert PDF to Text.
 */
public class PdfToText {

    public String pdf_to_text(String file_path) {
        try {
            File pdfFile = new File(file_path);
            PDDocument document = Loader.loadPDF(pdfFile);

            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            document.close();
            return(text);
        } catch (IOException e) {
            return "";
        }
    }
}
