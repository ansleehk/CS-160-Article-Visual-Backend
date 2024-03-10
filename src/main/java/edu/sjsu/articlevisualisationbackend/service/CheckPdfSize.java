package edu.sjsu.articlevisualisationbackend.service;

import org.springframework.stereotype.Service;
import java.util.StringTokenizer;

/**
 * Service _ Check PDF file's size.
 */
@Service
public class CheckPdfSize {

    public CheckPdfSize() {}

    public int check_pdf_size(String fileText) {
        StringTokenizer str_arr = new StringTokenizer(fileText, " \t\n\r\f,.:;?![]'");
        return str_arr.countTokens();
    }
}
