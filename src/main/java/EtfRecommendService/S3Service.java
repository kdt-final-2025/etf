package Etf;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
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

    /**
     * 기존 이미지를 삭제하고 새 이미지 업로드
     * @param multipartFile 새 이미지 파일
     * @param oldFileUrl 기존 이미지 URL (없으면 null)
     * @return 새 이미지 URL
     * @throws IOException
     */
    public String replaceFile(MultipartFile multipartFile, String oldFileUrl) throws IOException {
        // 1. 기존 파일 삭제
        if (oldFileUrl != null && !oldFileUrl.isEmpty()) {
            deleteFileFromUrl(oldFileUrl);
        }

        // 2. 새 파일 업로드
        return uploadFile(multipartFile);
    }

    /**
     * 새 이미지 업로드 (기존 방식)
     */
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        metadata.setHeader("Content-Disposition", "inline");

        try (InputStream inputStream = multipartFile.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    fileName,
                    inputStream,
                    metadata
            );

            amazonS3.putObject(putObjectRequest);
        }

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    /**
     * S3에서 파일 삭제 (URL 기반)
     */
    public void deleteFileFromUrl(String fileUrl) {
        String fileName = extractKeyFromUrl(fileUrl);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    /**
     * 전체 URL에서 key (객체 경로) 추출
     */
    private String extractKeyFromUrl(String fileUrl) {
        // 모든 도메인 패턴 대응 (s3.amazonaws.com, CloudFront 등)
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1); // 단순 객체 이름만 필요한 경우
    }

}