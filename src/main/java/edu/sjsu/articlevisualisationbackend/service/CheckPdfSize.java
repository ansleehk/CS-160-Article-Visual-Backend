package edu.sjsu.articlevisualisationbackend.service;

import org.springframework.stereotype.Service;
import java.util.StringTokenizer;

/**
 * Service _ Check PDF file's size.
 */
@Service
public class CheckPdfSize {
    private final int WORD_LIMIT = 3500;

    public CheckPdfSize() {}

    public int check_pdf_size(String fileText) throws InvalidPdfException {
        int count = count_word(fileText);
        if (count >= WORD_LIMIT) {
            throw new InvalidPdfException("PDF contains more than 3500 words.");
        }
        return count;
    }

    private int count_word(String fileText) {
        StringTokenizer str_arr = new StringTokenizer(fileText, " \t\n\r\f,.:;?![]'");
        return str_arr.countTokens();
    }
}
