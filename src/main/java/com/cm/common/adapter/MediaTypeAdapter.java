package com.cm.common.adapter;

import com.cm.common.model.enumeration.MediaType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MediaTypeAdapter implements AttributeConverter<MediaType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final MediaType mediaType) {
        return mediaType.getCode();
    }

    @Override
    public MediaType convertToEntityAttribute(final Integer code) {
        return MediaType.getByCode(code);
    }
}
