package com.tmesh.tmeshmall.thirdparty.common;



import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


@Data
public class AliUtils {

    private String endpoint;
    private String point;
    private Set<String> buckets;
    private String accessKeyId;
    private String accessKeySecret;
    private String callbackUrl;

    public String getBucket(String bucketName) {
        boolean contains = buckets.contains(bucketName);
        if (!contains) {
            return null;
        }
        return buckets.stream().filter(bucket ->bucket.equals(bucketName)).findFirst().get();
    }
    
    public boolean getDoesObjectExist(String key, String bucketName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        boolean objectExist = false;
        String bucket = getBucket(bucketName);
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
            if (getObjectRequest == null) {
                return objectExist;
            }
            objectExist = ossClient.doesObjectExist(getObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            ossClient.shutdown();
        }
        return objectExist;
    }
    
    public OSSObject getObjectByOss(String key, String bucketName) throws OSSException, IOException {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        OSSObject ossObject = null;
        String bucket = getBucket(bucketName);
        if (bucket == null) {
            return null;
        }
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
//        boolean objectExist = ossClient.doesObjectExist(getObjectRequest);
            ossObject = ossClient.getObject(getObjectRequest);
            ossObject.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            ossClient.shutdown();
        }
        return ossObject;
    }

    public Map<String, Object> alicloudOssGetObjectByOss(String key, String bucketName) throws IOException {
        AliOssGetStatusCode instance = null;
        OSSObject ossObject = null;
        String statusCode;
        String statusMessage;
        String hostId;
        String bucket = getBucket(bucketName);
        try {
            ossObject = this.getObjectByOss(key, bucket);
            if (ossObject == null) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", 400);
                map.put("successful", false);
                map.put("msg", "bucket不存在");
                return map;
            }
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
            e.printStackTrace();
            throw e;
        }
        Map<String, Object> map = Objects.requireNonNull(AliOssGetStatusCode.getInstance(statusCode)).
                successful(statusMessage, hostId);
        map.put("ossObject", ossObject);
        System.out.println(map);
        return map;
    }

    public BufferedReader downloadStreamByOss(String key, String bucketName) {
        BufferedReader reader = null;
        try {
            String bucket = getBucket(bucketName);
            // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
            OSSObject ossObject = this.getObjectByOss(key, bucket);
            if (ossObject == null) {
                return null;
            }
            // 读取文件内容。
            System.out.println("Object content:");
            reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                System.out.println("\n" + line);
            }
            // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
            reader.close();
            // ossObject对象使用完毕后必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。            
            ossObject.close();

        } catch (OSSException oe) {
            oe.printStackTrace();
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            oe.printStackTrace();
        } catch (Throwable ce) {
            ce.printStackTrace();
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            ce.printStackTrace();
        } finally {
            
        }
        return reader;
    }
    
    // 会替换原来已经存在的文件
    public void simpleUploadToOss(String key, InputStream inputStream, String bucketName) {
        // Endpoint以上海为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-shanghai.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "xxxxxxxxx";
//        String accessKeySecret = "xxxxxxxxx";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String bucket = getBucket(bucketName);
        if (bucket == null || bucket.isEmpty()) {
            ossClient.shutdown();
            return;
        }
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, inputStream);
            // 上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\Lenovo\\Desktop\\1234.jpg");
            
//            PutObjectResult putObjectResult = ossClient.putObject(bucket, key, inputStream);
            
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            System.out.println(putObjectResult.getResponse());
            
            // 关闭OSSClient。
            ossClient.shutdown();
            System.out.println("上传成功.");
        } catch (OSSException oe) {
            oe.printStackTrace();
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (Throwable ce) {
            ce.printStackTrace();
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    public void callbackUploadToOss(String key, InputStream inputStream, String bucketName) {
        // Endpoint以上海为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-shanghai.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "xxxxxxxxx";
//        String accessKeySecret = "xxxxxxxxx";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String bucket = getBucket(bucketName);
        if (bucket == null || bucket.isEmpty()) {
            ossClient.shutdown();
            return;
        }
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, inputStream);

            // 上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\Lenovo\\Desktop\\1234.jpg");
            PutObjectResult putObjectResult = ossClient.putObject(bucket, key, inputStream);

            // 上传回调参数。
            Callback callback = new Callback();
            callback.setCallbackUrl(callbackUrl);
            //（可选）设置回调请求消息头中Host的值，即您的服务器配置Host的值。
//             callback.setCallbackHost("yourCallbackHost");
            // 设置发起回调时请求body的值。
            callback.setCallbackBody("{\\\"mimeType\\\":${mimeType},\\\"size\\\":${size}}");
            // 设置发起回调请求的Content-Type。
            callback.setCalbackBodyType(Callback.CalbackBodyType.JSON);
            // 设置发起回调请求的自定义参数，由Key和Value组成，Key必须以x:开始。
            callback.addCallbackVar("x:var1", "value1");
            callback.addCallbackVar("x:var2", "value2");
            putObjectRequest.setCallback(callback);

            System.out.println(putObjectResult.getResponse());

            // 关闭OSSClient。
            ossClient.shutdown();
            System.out.println("上传成功.");
        } catch (OSSException oe) {
            oe.printStackTrace();
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (Throwable ce) {
            ce.printStackTrace();
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    public Map<String, String> getSign(String bucketName) {
        Map<String, String> respMap = new LinkedHashMap<>();
        String bucket = getBucket(bucketName);
        if (bucket == null || bucket.isEmpty()) {
            respMap.put("accessId", "");
            respMap.put("policy", "");
            respMap.put("signature", "");
            respMap.put("dir", "");
            respMap.put("host", "");
            respMap.put("expire", "");
            respMap.put("callback", "");
            respMap.put("msg", "bucket不存在");
            return respMap;
        }

        //http://tmeshmall-clouds.oss-cn-beijing.aliyuncs.com/iqiyi.png
        String host = "http://" + bucket + "." + point; // host的格式为 bucketname.endpoint

        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = format + "/"; // 用户上传文件时指定的前缀。

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            
            respMap.put("accessId", accessKeyId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            respMap.put("callback", callbackUrl);
            respMap.put("msg", "成功");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
        return respMap;
    }
    
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
