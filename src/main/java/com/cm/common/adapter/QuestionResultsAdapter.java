package com.cm.common.adapter;

import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.dto.QuestionResultDTO;
import com.cm.common.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Set;

@Converter
public class QuestionResultsAdapter implements AttributeConverter<Set<QuestionResultDTO>, String> {

    @Override
    public String convertToDatabaseColumn(final Set<QuestionResultDTO> questionResultDTOS) {
        return JsonUtils.serialize(questionResultDTOS);
    }

    @Override
    public Set<QuestionResultDTO> convertToEntityAttribute(final String answerJson) {
        return JsonUtils.deserialize(answerJson, new TypeReference<Set<QuestionResultDTO>>() {
        });
    }
}
