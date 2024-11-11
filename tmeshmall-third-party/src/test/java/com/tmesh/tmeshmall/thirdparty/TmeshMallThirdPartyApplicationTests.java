package com.tmesh.tmeshmall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.tmesh.tmeshmall.thirdparty.common.AliOssGetStatusCode;
import com.tmesh.tmeshmall.thirdparty.common.AliUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
class TmeshMallThirdPartyApplicationTests {

    @Autowired
    AliUtils aliUtils;

    @Test
    void testAliUtils() {
        System.out.println(aliUtils);
        System.out.println(aliUtils.getBucket("1"));;
    }
    
    @Test
    void alicloudOssSimpleUploadToOss() throws FileNotFoundException {
        // 创建PutObjectRequest对象:参数 bucket 和 文件名
        System.out.println(aliUtils.getBuckets());
        InputStream inputStream = new FileInputStream("C:\\Users\\Lenovo\\Desktop\\123.png");
        aliUtils.simpleUploadToOss("test.jpg", inputStream, "images");
        
    }
    @Test
    void alicloudOssGetObjectByOss() throws IOException {
        AliOssGetStatusCode instance = null;
        OSSObject ossObject = null;
        String statusCode;
        String statusMessage;
        String hostId;
        try {
            ossObject = aliUtils.getObjectByOss("test.jpg", "images");
            ResponseMessage responseMessage = ossObject.getResponse();
            int code = responseMessage.getStatusCode();
            if (code == 200) {
                statusCode = "Success";
                statusMessage = "成功";
            } else {
                statusCode = "Unkonwn";
                statusMessage = "未知错误";
            }
            hostId = responseMessage.getUri();
        } catch (OSSException e) {
            e.printStackTrace();
            statusCode = e.getErrorCode();
            statusMessage = e.getErrorMessage();
            hostId = e.getHostId();
        } catch (IOException e) {
            throw e;
        }
        Map<String, Object> map = Objects.requireNonNull(AliOssGetStatusCode.getInstance(statusCode)).
                successful(statusMessage, hostId);
        System.out.println(map);
    }
}
