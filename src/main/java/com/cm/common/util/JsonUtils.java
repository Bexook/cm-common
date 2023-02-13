package com.cm.common.util;

import com.cm.common.exception.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;


public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    public static <T> String serialize(final T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Unable to serialize object to string : {}", object);
            throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public static <T> T deserialize(final String json, final TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Unable to deserialize json to object : {}", json);
            throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
