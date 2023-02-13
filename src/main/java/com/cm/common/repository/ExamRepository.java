package com.cm.common.repository;

import com.cm.common.model.domain.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<ExamEntity, Long> {

    @Modifying
    @Query(name = "bindExamToCourse", nativeQuery = true)
    void bindExamToCourse(@Param("examId") final Long examId, @Param("courseId") final Long courseId);

    @Query(name = "findByCourseId", nativeQuery = true)
    ExamEntity findByCourseId(@Param("courseId") final Long courseId);

    @Query(name = "countQuestionsForExam", nativeQuery = true)
    Integer countQuestionsForExam(@Param("examId") final Long examId);

}
