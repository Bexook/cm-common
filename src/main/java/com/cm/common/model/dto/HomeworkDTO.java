package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class HomeworkDTO extends BaseDTO {

    @JsonProperty("appUser")
    private AppUserDTO appUser; //student
    @NotNull
    @JsonProperty("media")
    private MediaDTO media;
    @NotNull
    @JsonProperty("grade")
    private Integer grade;
    @NotNull
    @JsonProperty("teacher_notes")
    private String teacherNotes;
    @JsonProperty("evaluated")
    private boolean evaluated;

}
