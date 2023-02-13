package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppUserExamReferenceDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("appUser")
    private AppUserDTO appUser;
    @JsonProperty("course")
    private CourseDTO course;
    @JsonProperty("grade")
    private Integer grade;
    @JsonProperty("evaluationDate")
    private LocalDate evaluationDate;


}
