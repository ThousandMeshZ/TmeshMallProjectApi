package com.tmesh.tmeshmall.thirdparty.controller;

import com.tmesh.common.utils.R;
import com.tmesh.tmeshmall.thirdparty.common.AliUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OssController {

    @Autowired
    AliUtils aliUtils;

    @PostMapping("/oss/policy")
        public R policy(@RequestBody Map<String, Object> map) {
//        images
        String bucketName = (String) map.get("bucketName");
        if (bucketName == null || bucketName.isEmpty()) {
            bucketName = "images";
        }
        Map<String, String> respMap = aliUtils.getSign(bucketName);
        return R.ok().put("data", respMap);
    }

}
