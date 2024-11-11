package com.tmesh.common.utils;



import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


@Data
public class AliUtils {

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;
//    private static final String endpoint = "https://oss-cn-shanghai.aliyuncs.com";

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;
//    private static final String bucket = "images";

    @Value("${spring.cloud.alicloud.access-key}")
    private String AccessKeyID;
//    private static final String AccessKeyID = "xxxxxxxxx";

    @Value("${spring.cloud.alicloud.secret-key}")
    private String AccessKeySecret;
//    private static final String AccessKeySecret = "xxxxxxxxx";
    
    
    public static void main(String[] args) throws FileNotFoundException {
        // Endpoint以上海为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-shanghai.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "xxxxxxxxx";
        String accessKeySecret = "xxxxxxxxx";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\Lenovo\\Desktop\\1234.jpg");
        PutObjectResult putObjectResult = ossClient.putObject("images", "test.jpg", inputStream);

        System.out.println(putObjectResult.getResponse());

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功.");
    }
}
