package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QuestionResultDTO {

    @JsonProperty("index")
    private Integer index;
    @JsonProperty("amountOfPoints")
    private Integer amountOfPoints;
    @JsonProperty("userAnswer")
    private AnswerDTO userAnswer;

}
