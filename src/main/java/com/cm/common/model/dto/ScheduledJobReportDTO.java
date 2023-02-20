package com.cm.common.model.dto;

import com.cm.common.model.enumeration.JobStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ScheduledJobReportDTO {

    @JsonProperty("id")
    private Long id;
    @NotNull
    @JsonProperty("name")
    private String name;
    @NotNull
    @JsonProperty("occurDate")
    private LocalDateTime occurDate;
    @NotNull
    @JsonProperty("impactedAccountsCount")
    private Integer impactedAccountsCount;
    @NotNull
    @JsonProperty("status")
    private JobStatus status;
    @NotNull
    @JsonProperty("startBy")
    private String startBy;
    @JsonProperty("failure_reason")
    private String failureReason;

}
