package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Data
@Accessors(chain = true)
public class ErrorResponseDTO {

    @JsonProperty("message")
    private String message;
    @JsonProperty("httpStatus")
    private HttpStatus httpStatus;

}
