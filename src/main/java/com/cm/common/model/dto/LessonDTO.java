package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.cm.common.constant.ApplicationValidationConstants.GENERAL_TEXT_REGEX;
import static com.cm.common.constant.ApplicationValidationConstants.LESSON_INDEX_REGEX;
@Data
@EqualsAndHashCode(callSuper = true)
public class LessonDTO extends BaseDTO {
    @NotNull
    @JsonProperty("subject")
    @Pattern(regexp = GENERAL_TEXT_REGEX)
    private String subject;
    @NotNull
    @JsonProperty("text")
    @Pattern(regexp = GENERAL_TEXT_REGEX)
    private String text;
    @NotNull
    @JsonProperty("index")
    private Integer index;
    @JsonProperty("media")
    private List<MediaDTO> media;
}
