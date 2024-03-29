package edu.sjsu.articlevisualisationbackend.service;
import edu.sjsu.articlevisualisationbackend.service.exception.InvalidPdfException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

public class CheckPdfSizeTest {
    @InjectMocks
    private CheckPdfSize checkPdfSize;

    public CheckPdfSizeTest() {
        checkPdfSize = new CheckPdfSize();
    }

    @Test
    public void testCheckPdfSize() throws InvalidPdfException {
        final String STRING = "Is it a file? It's a string.";
        final int EXPECTED_OUTPUT = 8;

        final int test = checkPdfSize.check_pdf_size(STRING);

        assertEquals(EXPECTED_OUTPUT, test);
    }
}
