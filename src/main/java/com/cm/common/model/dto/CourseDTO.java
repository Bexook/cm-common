package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Pattern;
import java.util.List;

import static com.cm.common.constant.ApplicationConstants.GENERAL_TEXT_REGEX;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CourseDTO extends BaseDTO {
    @NonNull
    @JsonProperty("subject")
    @Pattern(regexp = GENERAL_TEXT_REGEX)
    private String subject;
    @NonNull
    @JsonProperty("description")
    @Pattern(regexp = GENERAL_TEXT_REGEX)
    private String description;
    @JsonProperty("coursePrinciple")
    private AppUserDTO coursePrinciple;
    @JsonProperty("lessons")
    private List<LessonDTO> lessons;
    @JsonProperty("available")
    private boolean available;
    @ToString.Exclude
    @JsonProperty("exam")
    private ExamDTO exam;
    @JsonProperty("amountOfPoints")
    private Integer amountOfPoints;

}
