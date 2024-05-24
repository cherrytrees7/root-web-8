package meeting.component;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@Service
public class MinioService {


    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.readPath}")
    private String readPath;

    public String getEndpoint() {
        return endpoint;
    }

    public String getBucketName() {
        return bucketName;
    }


    public String uploadFile(String path, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // 确保 bucket 存在
            if (!minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(io.minio.MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .stream(is, -1, 10485760) // -1 means unknown size
                            .contentType(file.getContentType())
                            .build()
            );

            // 构建文件的访问URL
            return readPath + "/" + bucketName + "/" + path;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Minio", e);
        }
    }
}
