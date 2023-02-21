package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.cm.common.constant.ApplicationConstants.GENERAL_TEXT_REGEX;
import static com.cm.common.constant.ApplicationConstants.LESSON_INDEX_REGEX;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"media"})
public class LessonDTO extends BaseDTO {
    @NotNull
    @JsonProperty("subject")
    @Pattern(regexp = GENERAL_TEXT_REGEX, message = "Wrong lesson subject")
    private String subject;
    @NotNull
    @JsonProperty("text")
    @Pattern(regexp = GENERAL_TEXT_REGEX, message = "Bad message text")
    private String text;
    @NotNull
    @JsonProperty("index")
    private Integer index;
}
