package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Pattern;

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
    @JsonProperty("available")
    private boolean available;
    @JsonProperty("amountOfPoints")
    private Integer amountOfPoints;

}
