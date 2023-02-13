package com.cm.common.adapter;

import com.cm.common.model.enumeration.TokenType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class TokenTypeAdapter implements AttributeConverter<TokenType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final TokenType tokenType) {
        return tokenType.getCode();
    }

    @Override
    public TokenType convertToEntityAttribute(final Integer integer) {
        return TokenType.getByCode(integer);
    }
}
