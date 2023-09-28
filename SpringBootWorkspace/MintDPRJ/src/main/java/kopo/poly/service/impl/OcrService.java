package kopo.poly.service.impl;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import kopo.poly.service.IOcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
@Service
public class OcrService implements IOcrService {

    //이미지 파일 경로를 매개변수로 받음
    public String detectText(MultipartFile multipartFile) throws IOException {
        log.info(this.getClass().getName() + ".getOcrText Start!!");
        //결과 문자열이 담길 변수
        String resultOcrProc = "";
        // Google Cloud Vision API 클라이언트 초기화
        try (ImageAnnotatorClient visionClient = ImageAnnotatorClient.create()) {

            // 이미지 파일을 바이트 배열로 읽기
            byte[] imageBytes = multipartFile.getBytes();

            // Vision API 요청에 사용할 이미지 생성
            ByteString imageContent = ByteString.copyFrom(imageBytes);
            Image image = Image.newBuilder().setContent(imageContent).build();

            // Vision API 요청 구성
            Feature feature = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
            BatchAnnotateImagesRequest batchRequest =
                    BatchAnnotateImagesRequest.newBuilder().addRequests(request).build();

            // Vision API 호출 및 응답 받기
            BatchAnnotateImagesResponse batchResponse = visionClient.batchAnnotateImages(batchRequest);
            AnnotateImageResponse response = batchResponse.getResponses(0);

            // 추출된 텍스트 출력
            resultOcrProc = response.getFullTextAnnotation().getText();
            log.info("Extracted Text:");
            log.info(resultOcrProc);

        } catch (IOException e) {
            log.info("Error reading image file: " + e.getMessage());
        } catch (Exception e) {
            log.info("Error performing text extraction: " + e.getMessage());
        } finally {
            log.info(this.getClass().getName() + ".getOcrText End!!");
            return resultOcrProc;
        }
    }

}