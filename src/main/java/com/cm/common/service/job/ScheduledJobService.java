package com.cm.common.service.job;

import com.cm.common.model.dto.ScheduledJobReportDTO;

import java.util.Set;

public interface ScheduledJobService {

    void saveReport(final ScheduledJobReportDTO scheduledJobReport);

    Set<ScheduledJobReportDTO> getAllReportsByName(final String jobName);

    Set<String> getJobNames();

    ScheduledJobReportDTO runJobByName(final String jobName);
}
