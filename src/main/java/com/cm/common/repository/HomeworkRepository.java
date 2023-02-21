package com.cm.common.repository;

import com.cm.common.model.domain.HomeworkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface HomeworkRepository extends JpaRepository<HomeworkEntity, Long> {

    @Modifying
    @Query(value = "UPDATE management.homework SET media_id = :mediaId WHERE id = :homeworkId", nativeQuery = true)
    void bindMediaToHomework(final Long mediaId, final Long homeworkId);


    @Query(name = "getHomeworksForLessonByLessonIdAndEvaluatedFlagValue", nativeQuery = true)
    Set<HomeworkEntity> getHomeworksForLessonByLessonIdAndEvaluatedFlagValue(@Param("lessonId") final Long lessonId, @Param("evaluated") final boolean evaluated);

    @Query(name = "getCountedHomeworkGradeForAllLessonsWithEvaluatedFlagTrueAndCourseId", nativeQuery = true)
    List<Integer> getHomeworkGradesForCourseByCourseId(@Param("courseId") final Long courseId, @Param("userId") final Long userId);

}
