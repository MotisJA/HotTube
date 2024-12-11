import com.hotsharp.user.utils.MinioUtil;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
public class MinioConnectionTest {

//    @Autowired
//    private MinioUtil minioUtil;

    public static void main(String[] args) {
        try {
            // 创建 MinioClient 对象
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://47.121.126.119:9090/") // 替换为你的 MinIO 服务器地址和端口
                    .credentials("dBx9QNbiq9ztwqRChXXO", "jeC6bk0rCHyFWn08uKUxSp2cVaAG67t2Jkkc5nqP") // 替换为你的访问密钥和秘密密钥
                    .build();

            // 尝试列出存储桶
            boolean isConnected = minioClient.listBuckets().size() >= 0;

            if (isConnected) {
                System.out.println("MinIO 连接正常");
            } else {
                System.out.println("MinIO 连接失败");
            }
        } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}