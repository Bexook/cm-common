package com.cm.common.adapter;

import com.cm.common.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;
import java.util.Objects;

@Converter
public class CertificateKeysListAdapter implements AttributeConverter<List<String>, String> {

    private Logger LOGGER = LoggerFactory.getLogger(CertificateKeysListAdapter.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(final List<String> strings) {
        return JsonUtils.serialize(strings);
    }

    @Override
    public List<String> convertToEntityAttribute(final String dbValue) {
        if (Objects.isNull(dbValue)) {
            return List.of();
        }
        return JsonUtils.deserialize(dbValue, new TypeReference<List<String>>() {
        });
    }
}
