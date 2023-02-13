package com.cm.common.repository;

import com.cm.common.model.domain.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT count(l.id) FROM management.lesson l WHERE l.course_id=:courseId")
    Integer countByCourseId(@Param("courseId") final Long courseId);

    @Modifying
    @Query(value = "UPDATE management.lesson l  SET course_id = :courseId WHERE l.id = :lessonId", nativeQuery = true)
    void bindLessonToCourse(@Param("lessonId") final Long lessonId, @Param("courseId") final Long courseId);

    @Query(name = "getAllLessonIdsForCourseId", nativeQuery = true)
    List<Long> getAllLessonIdsForCourse(@Param("courseId") final Long courseId);

    boolean existsBySubject(final String subject);

}
