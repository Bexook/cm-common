package com.cm.common.model.domain;

import com.cm.common.model.enumeration.JobStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Setter
@Getter
@Accessors(chain = true)
@Table(name = "scheduled_job", schema = "system")
public class ScheduledJobReportEntity {

    @Id
    @Column(name = "id", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "occur_date")
    private LocalDateTime occurDate;
    @Column(name = "impacted_accounts_count")
    private Integer impactedAccountsCount;
    @Column(name = "status")
    private JobStatus status;
    @Column(name = "start_by")
    private String startBy;
    @Column(name = "failure_reason")
    private String failureReason;


    @PrePersist
    public void prePersist() {
        if (Objects.isNull(occurDate)) {
            occurDate = LocalDateTime.now();
        }
    }
}
