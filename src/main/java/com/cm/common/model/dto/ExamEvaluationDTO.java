package com.cm.common.model.dto;

import com.cm.common.model.enumeration.ExamStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class ExamEvaluationDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("appUser")
    private AppUserDTO appUser;
    @JsonProperty("exam")
    private ExamDTO exam;
    @JsonProperty("userResults")
    private Set<QuestionResultDTO> userResults;
    @JsonProperty("status")
    private ExamStatus examStatus;

}
