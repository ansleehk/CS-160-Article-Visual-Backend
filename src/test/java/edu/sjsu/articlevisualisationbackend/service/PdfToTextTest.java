package edu.sjsu.articlevisualisationbackend.service;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

public class PdfToTextTest {
    @InjectMocks
    private PdfToText pdfToText;

    public PdfToTextTest() {
        pdfToText = new PdfToText();
    }

    @Test
    public void testPdfToText() {
        final String PDF_FILE_PATH = "src/test/resources/test.pdf";
        final String EXPECTED_TEXT = "Test PDF\n";

        final String text = pdfToText.pdf_to_text(PDF_FILE_PATH);

        assertEquals(EXPECTED_TEXT, text);
    }

}
