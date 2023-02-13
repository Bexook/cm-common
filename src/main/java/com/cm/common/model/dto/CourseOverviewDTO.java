package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CourseOverviewDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("description")
    private String description;
    @JsonProperty("lessonAmount")
    private Integer lessonAmount;


}
