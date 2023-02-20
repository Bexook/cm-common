package com.cm.common.repository;

import com.cm.common.model.domain.ScheduledJobReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ScheduledJobReportRepository extends JpaRepository<ScheduledJobReportEntity, Long> {

    @Query("SELECT DISTINCT job.name FROM ScheduledJobReportEntity job")
    Set<String> getScheduledJobNames();

}
