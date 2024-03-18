package edu.sjsu.articlevisualisationbackend.service;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PdfToTextTest {

    @Test
    public void testPdfToText() throws IOException {
        final String PDF_FILE_PATH = "src/test/resources/test.pdf";
        final String EXPECTED_TEXT = "Test PDF\n";

        final PdfToText pdfToText = new PdfToText(PDF_FILE_PATH);
        final String text = pdfToText.getPdfTextContent();

        assertEquals(EXPECTED_TEXT, text);
    }

}
