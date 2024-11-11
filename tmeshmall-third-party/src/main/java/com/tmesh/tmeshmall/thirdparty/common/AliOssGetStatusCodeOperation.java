package com.tmesh.tmeshmall.thirdparty.common;

import java.util.Map;

public interface AliOssGetStatusCodeOperation {
    // 返回信息
    Map<String, Object> successful(String statusMessage, String hostId);
}
