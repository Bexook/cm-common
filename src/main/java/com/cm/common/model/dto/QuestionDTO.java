package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.cm.common.constant.ApplicationConstants.GENERAL_TEXT_REGEX;

@Getter
@Setter
@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionDTO extends BaseDTO {

    @NotNull
    @JsonProperty("questionText")
    @Pattern(regexp = GENERAL_TEXT_REGEX)
    private String questionText;
    @NotNull
    @JsonProperty("amountOfPoints")
    private Integer amountOfPoints;
    @NotNull
    @JsonProperty("index")
    private Integer index;
    @JsonProperty("answers")
    private List<AnswerDTO> answers;

}
