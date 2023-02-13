package com.cm.common.model.dto;

import com.cm.common.model.enumeration.MediaType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MediaDTO extends BaseDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("mediaType")
    private MediaType mediaType;
    @JsonProperty("key")
    private String key;
}
