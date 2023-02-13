package com.cm.common.adapter;

import com.cm.common.model.enumeration.UserRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class UserRoleAdapter implements AttributeConverter<UserRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(final UserRole userRole) {
        return userRole.getCode();
    }

    @Override
    public UserRole convertToEntityAttribute(final Integer code) {
        return UserRole.getByCode(code);
    }
}
