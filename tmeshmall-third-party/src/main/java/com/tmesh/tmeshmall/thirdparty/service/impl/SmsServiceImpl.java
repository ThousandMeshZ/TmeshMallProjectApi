package com.tmesh.tmeshmall.thirdparty.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tmesh.common.utils.HttpUtils;
import com.tmesh.tmeshmall.thirdparty.service.SmsService;
import lombok.Data;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信服务实现类
 *
 * @Author: TMesh
 * @Date: 2024/01/27 22:58
 */
@Data
public class SmsServiceImpl implements SmsService {

    private String host;
    private String path;
    private String appcode;
    private String method;
    private String skin;

    @Override
    public Boolean sendCode(String phone, String code) {
        try {
            Map<String, String> headers = new HashMap<String, String>();
            //最后在 header 中的格式(中间是英文空格)为 Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            Map<String, String> querys = new HashMap<String, String>();
            querys.put("mobile", phone);
            /**
             *     {
             *       "Id": 3,
             *       "TContent": "【智能云】您的验证码是#code#。如非本人操作，请忽略本短信",
             *       "Status": 1,
             *       "NotPassReason": null
             *     },
             *     {
             *       "Id": 6057,
             *       "TContent": "【TMeshMall】您的验证码是#code#。如非本人操作，请忽略本短信。",
             *       "Status": 1,
             *       "NotPassReason": null
             *     },
             *     {
             *       "Id": 6058,
             *       "TContent": "【TMeshMall】[TMeshMall]您的验证码是#code#。如非本人操作，请忽略本短信",
             *       "Status": 1,
             *       "NotPassReason": null
             *     },
             *     {
             *       "Id": 6062,
             *       "TContent": "【TMeshMall】您的验证码是#code#。",
             *       "Status": 1,
             *       "NotPassReason": null
             *     },
             *     {
             *       "Id": 6063,
             *       "TContent": "【TMeshMall】TMeshMall您的验证码是#code#。如非本人操作，请忽略本短信。",
             *       "Status": 1,
             *       "NotPassReason": null
             *     }
             * */
            querys.put("content", "【TMeshMall】您的验证码是" + code + "。如非本人操作，请忽略本短信。");
            // (中间是英文空格)
            HttpResponse response = HttpUtils.doGet(host, path, headers, querys);
            JSONObject json = read(response.getEntity().getContent());
            System.out.print("获取返回的json: " + json);
            if (response.getStatusLine().getStatusCode() == 200) {
                String error_code = json.getString("error_code");
                if ("0".equals(error_code)) {
                    System.out.println("正常请求计费(其他均不计费)");
                } else if ("201709".equals(error_code)) {
                    System.out.println("发送内容和模板不匹配,发送的内容和已审核通过模板不匹配,发送内容需和已审核通过的模板除了变量部分，其余部分必须一致");
                } else if ("201708".equals(error_code)) {
                    System.out.println("模板参数没有全部生效,短信内容不能包含特殊字符#,请检查参数重试");
                } else if ("201705".equals(error_code)) {
                    System.out.println("参数错误");
                } else if ("201706".equals(error_code)) {
                    System.out.println("短信长度超过限制");
                } else if ("201701".equals(error_code)) {
                    System.out.println("错误的手机号码");
                } else if ("201711".equals(error_code)) {
                    System.out.println("变量内容超过限定字符");
                } else if ("201710".equals(error_code)) {
                    System.out.println("有效号码不足");
                } else {
                    System.out.println("未知错误");
                }
            } else {
                System.out.println("未知错误");
            }
        } catch (MalformedURLException e) {
            System.out.println("URL格式错误");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.out.println("URL地址错误");
            e.printStackTrace();
        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            e.printStackTrace();
        }
        return true;
    }

    /*
     * 读取返回结果
     */
    private static JSONObject read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return JSONObject.parseObject(sb.toString());
    }
}