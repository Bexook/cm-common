package com.cm.common.service.job.impl;

import com.cm.common.constant.ApplicationConstants;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.ScheduledJobReportEntity;
import com.cm.common.model.dto.ScheduledJobReportDTO;
import com.cm.common.repository.ScheduledJobReportRepository;
import com.cm.common.service.SchedulerAuthenticationExecutor;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.job.ScheduledJobService;
import com.cm.common.service.user.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledJobServiceImpl implements ScheduledJobService {


    private final ScheduledJobReportRepository scheduledJobReportRepository;
    private final OrikaBeanMapper mapper;

    private final CourseService courseService;
    private final AppUserService appUserService;

    private final Map<String, SchedulerAuthenticationExecutor> jobs = new HashMap<>();


    @PostConstruct
    protected void postConstruct() {
        jobs.put(ApplicationConstants.ACCOUNT_DELETION_JOB_NAME, appUserService::dropNotVerifiedUsers);
        jobs.put(ApplicationConstants.ACCOUNT_DELETION_WARNING_JOB_NAME, appUserService::sendAccountDeletionWarningNotification);
        jobs.put(ApplicationConstants.LESSON_PROGRESS_JOB_NAME, courseService::progressCourseLessonIndex);
    }


    @Override
    public void saveReport(@Valid final ScheduledJobReportDTO scheduledJobReport) {
        scheduledJobReportRepository.save(mapper.map(scheduledJobReport, ScheduledJobReportEntity.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ScheduledJobReportDTO> getAllReportsByName(final String jobName) {
        return mapper.mapAsSet(scheduledJobReportRepository.findAll(Example.of(new ScheduledJobReportEntity().setName(jobName))), ScheduledJobReportDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getJobNames() {
        return scheduledJobReportRepository.getScheduledJobNames();
    }

    @Override
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public ScheduledJobReportDTO runJobByName(final String jobName) {
        final ScheduledJobReportDTO report = jobs.get(jobName).runAsJob();
        return mapper.map(scheduledJobReportRepository.save(mapper.map(report, ScheduledJobReportEntity.class)), ScheduledJobReportDTO.class);

    }
}
