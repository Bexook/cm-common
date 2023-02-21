package com.cm.common.model.dto;

import com.cm.common.model.enumeration.MediaType;
import com.cm.common.model.enumeration.MediaUploadStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class MediaDTO extends BaseDTO {
    @NotNull
    @JsonProperty("mediaType")
    private MediaType mediaType;
    @JsonProperty("key")
    private String key;
    @JsonProperty("uploadStatus")
    private MediaUploadStatus uploadStatus;
    @JsonProperty("lesson")
    private LessonDTO lesson;
    @JsonProperty("url")
    private String url;
}
