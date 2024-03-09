package edu.sjsu.articlevisualisationbackend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class SavePdfToDisk {

    private String tempOsDirectory;
    private String tempFilePath;
    private MultipartFile originalFile;

    public SavePdfToDisk() {
        this.setTempOsDirectoryByDefault();
    }

    public void setOriginalFile(MultipartFile originalFile) {
        this.originalFile = originalFile;
    }

    private void setTempOsDirectoryByDefault() {
        // Get the temporary directory
        this.tempOsDirectory = this.OsDirectory();
    }

    private String OsDirectory() {
        // Get the OS directory

        String tempDir = System.getProperty("java.io.tmpdir");
        return tempDir;
    }

    private String generateFileUuidName(){
        // Generate a UUID for the file name
        return UUID.randomUUID().toString() + ".pdf";

    }

    public String saveTempPdf() throws Exception{
        // Save the file to the temporary directory
        String fileName = this.generateFileUuidName();
        this.tempFilePath = this.tempOsDirectory + fileName;
        File file = new File(this.tempFilePath);

        this.originalFile.transferTo(file);
        return this.tempFilePath;
    }

    public void removeTempPdf() {
        // Remove the file from the temporary directory
        File file = new File(this.tempFilePath);
        file.delete();
    }

}
