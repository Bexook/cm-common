package com.cm.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.cm.common.constant.ApplicationValidationConstants.GENERAL_TEXT_REGEX;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AnswerDTO extends BaseDTO {

    @NotNull
    @Pattern(regexp = GENERAL_TEXT_REGEX)
    private String answerValue;
    private boolean rightAnswer;
}
