package Etf;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

        // 메타데이터 설정(이미지가 다운로드 되지않도록 inline으로)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        metadata.setHeader("Content-Disposition", "inline");

        // 스트림으로 직접 업로드
        try (InputStream inputStream = multipartFile.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    fileName,
                    inputStream,
                    metadata);

            amazonS3.putObject(putObjectRequest);
        }

        // 이미지 URL 생성
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

//    private File multiPartFileToFile(MultipartFile file) throws IOException {
//        File convertedFile = new File(file.getOriginalFilename());
//        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)) {
//            fileOutputStream.write(file.getBytes());
//        }
//        return convertedFile;
//    }

}