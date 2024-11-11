package com.tmesh.tmeshmall.thirdparty.common;

import java.util.HashMap;
import java.util.Map;

public enum AliOssGetStatusCode implements AliOssGetStatusCodeOperation {
    // 200 成功
    CODE200("Success") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 200);
            map.put("successful", true);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    // 304
    CODE304("Not Modified") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 304);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    CODE400("InvalidTargetType") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 400);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    CODE403("InvalidObjectState") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 403);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    CODE404_1("NoSuchKey") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 404);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    CODE404_2("Not Found") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 404);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    CODE404_3("SymlinkTargetNotExist") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 404);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    CODE405("Method Not Allowed") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 405);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    CODE412("Precondition Failed") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 412);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    },

    CODE600("Unkonwn") {
        @Override
        public Map<String, Object> successful(String statusMessage, String hostId) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 600);
            map.put("successful", false);
            map.put("msg", statusMessage);
            map.put("hostId", hostId);
            return map;
        }
    };

    String errorCode;

    AliOssGetStatusCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public static AliOssGetStatusCode getInstance(String errorCode) {
        for (AliOssGetStatusCode getStatusCode : AliOssGetStatusCode.values()) {
            if (getStatusCode.errorCode.equals(errorCode)) {
                return getStatusCode;
            }
        }
        return null;
    }
}
