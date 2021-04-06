package com.zykj.pointsmall.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * jackson util
 *
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2021-01-04
 */
@Slf4j
public class JsonUtil {

    private final static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        // 忽略json中在对象不存在对应属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略空bean转json错误
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * json转对象
     *
     * @param jsonStr   json串
     * @param classType 对象类型
     * @return 对象
     */
    public static <T> T toEntity(String jsonStr, Class<T> classType) {

        if (StringUtils.isEmpty(jsonStr)) {
            log.warn("Json string {} is empty!", classType);
            return null;
        }

        try {
            return mapper.readValue(jsonStr, classType);
        } catch (IOException e) {
            log.error("json to entity error.", e);
        }
        return null;
    }

    /**
     * json转化为带泛型的对象
     *
     * @param jsonStr json字符串
     * @param typeReference 转化类型
     * @return 对象
     */
    public static <T> T toEntity(String jsonStr, TypeReference<T> typeReference) {
        if (!StringUtils.hasLength(jsonStr) || typeReference == null) {
            return null;
        }
        try {
            return (T) mapper.readValue(jsonStr, typeReference);
        } catch (JsonProcessingException e) {
            log.error("json to entity error.", e);
        }
        return null;
    }

    /**
     * 对象转json
     *
     * @param obj 对象
     * @return json串
     */
    public static String toJson(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("obj to json error.", e);
        }
        return null;
    }

    /**
     * 对象转json(格式化的json)
     *
     * @param obj 对象
     * @return 格式化的json串
     */
    public static String toJsonWithFormat(Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        }

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.error("obj to json error.", e);
        }
        return null;
    }
}
