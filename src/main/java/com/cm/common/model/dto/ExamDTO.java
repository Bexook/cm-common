package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExamDTO extends BaseDTO {

    @JsonProperty("course")
    private CourseDTO course;
    @JsonProperty("minGrade")
    private Integer minGrade; //minimumSucceedGrade
    @JsonProperty("maxGrade")
    private Integer maxGrade;
    @JsonProperty("questions")
    private List<QuestionDTO> questions;

}
