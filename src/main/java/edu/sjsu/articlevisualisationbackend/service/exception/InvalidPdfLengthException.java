package edu.sjsu.articlevisualisationbackend.service.exception;

import edu.sjsu.articlevisualisationbackend.service.CheckPdfSize;

public class InvalidPdfLengthException extends InvalidPdfException {

    String errorMsg = "The PDF is too long. The maximum length is %s tokens. You have %s tokens. Please upload a shorter PDF.";

    public InvalidPdfLengthException(int pdfTokenCount) {

        super("The PDF is too long.");


        final int PDF_MAX_TOKEN_LIMIT = CheckPdfSize.PDF_MAX_TOKEN_LIMIT;
        errorMsg = String.format(errorMsg, PDF_MAX_TOKEN_LIMIT, pdfTokenCount);

    }

    @Override
    public String getMessage() {
        return errorMsg;
    }


}
