package com.cm.common.repository;

import com.cm.common.model.domain.ExamEvaluationEntity;
import com.cm.common.model.enumeration.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ExamEvaluationRepository extends JpaRepository<ExamEvaluationEntity, Long> {

    @Query(value = "SELECT * FROM management.exam_results er WHERE er.user_id = :userId AND er.id = :examId AND er.status = :#{#status.name()}", nativeQuery = true)
    Set<ExamEvaluationEntity> getAllRecordsByExamIdAndUserIdAndStatus(@Param("examId") final Long examId, @Param("userId") final Long userId, @Param("status") final ExamStatus status);

}
