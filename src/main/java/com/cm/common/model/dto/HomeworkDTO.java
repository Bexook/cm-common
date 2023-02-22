package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, exclude = {"media"})
public class HomeworkDTO extends BaseDTO {
    @NotNull
    @ToString.Exclude
    @JsonProperty("media")
    private MediaDTO media;
    @NotNull
    @JsonProperty("grade")
    private Integer grade;
    @NotNull
    @JsonProperty("teacherNotes")
    private String teacherNotes;
    @JsonProperty("evaluated")
    private boolean evaluated;

}
