package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("updatedDate")
    private LocalDateTime updatedDate;
    @JsonProperty("createdDate")
    private LocalDateTime createdDate;

}
