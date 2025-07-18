package com.heval.ecommerce.services;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

public interface S3Service {

    public String uploadFile(MultipartFile file, String folder);
    void deleteFile(String key);
    String uploadPdf(byte[] pdfBytes, String filename, String folder);
    S3Client getClient();


}
