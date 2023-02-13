package com.cm.common.model.dto;


import com.cm.common.model.enumeration.CourseProgressStatus;
import com.cm.common.model.enumeration.ExamStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserEvaluationResultDTO {

    @JsonProperty("grade")
    private Integer grade;
    @JsonProperty("courseProgressStatus")
    private CourseProgressStatus courseProgressStatus;
    @JsonProperty("examStatus")
    private ExamStatus examStatus;
}
