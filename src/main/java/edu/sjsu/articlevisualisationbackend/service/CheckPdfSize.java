package edu.sjsu.articlevisualisationbackend.service;

import edu.sjsu.articlevisualisationbackend.service.exception.InvalidPdfException;
import edu.sjsu.articlevisualisationbackend.service.exception.InvalidPdfLengthException;
import org.springframework.stereotype.Service;
import java.util.StringTokenizer;

/**
 * Service _ Check PDF file's size.
 */
@Service
public class CheckPdfSize {
    public static final int PDF_MAX_TOKEN_LIMIT = 3500;

    public CheckPdfSize() {

    }

    public int check_pdf_size(String fileText) throws InvalidPdfException {
        int count = count_word(fileText);

        if (count >= PDF_MAX_TOKEN_LIMIT) {
            throw new InvalidPdfLengthException(count);
        }
        return count;
    }

    private int count_word(String fileText) {
        StringTokenizer str_arr = new StringTokenizer(fileText, " \t\n\r\f,.:;?![]'");
        return str_arr.countTokens();
    }
}
