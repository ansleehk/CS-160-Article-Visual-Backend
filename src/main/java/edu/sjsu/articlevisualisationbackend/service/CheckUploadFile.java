package edu.sjsu.articlevisualisationbackend.service;

import edu.sjsu.articlevisualisationbackend.service.exception.InvalidUploadFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CheckUploadFile {


    public void checkUploadFile(MultipartFile file) throws InvalidUploadFileException {
        final boolean isPdf = this.isPdf(file);
        if  (!isPdf) {
            throw new InvalidUploadFileException("File is not a PDF");
        }
    }
    private boolean isPdf(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }
}
