package com.cm.common.service;

import com.cm.common.model.dto.ScheduledJobReportDTO;

@FunctionalInterface
public interface SchedulerAuthenticationExecutor {
    ScheduledJobReportDTO runAsJob();

}
