package com.tmesh.common.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GenerateResultUtils {
    
    public void getMapResult(Map<String, Object> map) {
        Object[] keySet = map.keySet().toArray();
        for (Object key: keySet) {
            String camelCase = StrUtil.toCamelCase(((String) key).toLowerCase());
            map.put(camelCase, map.remove(key));
        }
    }

    public void getMapResult(List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            Object[] keySet = map.keySet().toArray();
            for (Object key: keySet) {
                String camelCase = StrUtil.toCamelCase(((String) key).toLowerCase());
                map.put(camelCase, map.remove(key));
            }
        }
    }
    
    public <E> Map<String, Object> getResult(E e) throws IllegalAccessException {
        Map<String, Object> result = new HashMap<>();
        for (Field field : e.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(e);
            if (value != null) {
                result.put(field.getName(), value);
            }
        }
        result.remove("serialVersionUID");
        return result;
    }
    
    public <E> Map<String, Object> getResult(E e, String ...selectKeys) throws IllegalAccessException {
        Map<String, Object> result = new HashMap<>();
        if (EmptyUtils.isEmpty(selectKeys)) {
            return result;
        }
        String[] lowSelectKeys = new String[selectKeys.length];
        for (int i = 0; i < lowSelectKeys.length; i++) {
            lowSelectKeys[i] = selectKeys[i].toLowerCase().replaceAll("_","");
        }
        for (Field field : e.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            for (String key : lowSelectKeys) {
                if (fieldName.toLowerCase().equals(key)) {
                    field.setAccessible(true);
                    Object value = field.get(e);
                    result.put(fieldName, value);
                }
            }
        }
        result.remove("serialVersionUID");
        return result;
    }

    public <E> List<Map<String, Object>> getResult(List<E> list) throws IllegalAccessException {
        List<Map<String, Object>> result = new ArrayList<>();
        for (E e : list) {
            Map<String, Object> map = new HashMap<>();
            for (Field field : e.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(e);
                if (value != null) {
                    map.put(field.getName(), value);
                }
            }
            result.add(map);
        }
        result.remove("serialVersionUID");
        return result;
    }

    public <E> List<Map<String, Object>> getResult(List<E> list, String  ...selectKeys) throws IllegalAccessException {
        List<Map<String, Object>> result = new ArrayList<>();
        if (EmptyUtils.isEmpty(selectKeys)) {
            return result;
        }
        String[] lowSelectKeys = new String[selectKeys.length];
        for (int i = 0; i < lowSelectKeys.length; i++) {
            lowSelectKeys[i] = selectKeys[i].toLowerCase().replaceAll("_","");
        }
        for (E e : list) {
            Map<String, Object> map = new HashMap<>();
            for (Field field : e.getClass().getDeclaredFields()) {
                String fieldName = field.getName();
                for (String key : lowSelectKeys) {
                    if (fieldName.toLowerCase().equals(key)) {
                        field.setAccessible(true);
                        Object value = field.get(e);
                        map.put(fieldName, value);
                    }
                }
            }
            result.add(map);
        }
        result.remove("serialVersionUID");
        return result;
    }
}
