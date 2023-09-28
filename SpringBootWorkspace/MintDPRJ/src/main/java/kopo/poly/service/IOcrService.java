package kopo.poly.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IOcrService {

    String detectText(MultipartFile multipartFile) throws IOException;
}
