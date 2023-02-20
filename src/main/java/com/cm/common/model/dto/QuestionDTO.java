package com.cm.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.cm.common.constant.ApplicationConstants.GENERAL_TEXT_REGEX;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionDTO extends BaseDTO {

    @NotNull
    @Pattern(regexp = GENERAL_TEXT_REGEX)
    private String questionText;
    private Integer amountOfPoints;
    @NotNull
    private Integer index;
    private List<AnswerDTO> answers;

}
