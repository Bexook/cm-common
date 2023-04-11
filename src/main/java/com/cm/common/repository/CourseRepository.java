package com.cm.common.repository;

import com.cm.common.model.domain.CourseEntity;
import com.cm.common.model.domain.UserCourseReference;
import com.cm.common.model.enumeration.CourseProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    List<CourseEntity> findAllByAvailable(final boolean available);

    @Query(value = "SELECT count('any') > 0 FROM management.app_user_course_reference aucr  WHERE aucr.course_id = :courseId AND aucr.app_user_id = :userId ", nativeQuery = true)
    boolean isUserAlreadyRegisteredToCourse(final Long userId, final Long courseId);


    @Query(value = "SELECT count('any') FROM management.app_user_course_reference aucr WHERE aucr.course_id = :courseId AND aucr.app_user_id = :userId AND aucr.status = :#{#status.getCode()}", nativeQuery = true)
    Integer countUserCoursesByStatus(@Param("userId") final Long userId,
                                     @Param("courseId") final Long courseId,
                                     @Param("status") final CourseProgressStatus status);

    @Modifying
    @Query("UPDATE CourseEntity SET available = :status WHERE id = :courseId")
    void updateCourseAvailabilityStatus(final Long courseId, final boolean status);

    @Query(name = "getAllUserCourseReferences", nativeQuery = true)
    Set<UserCourseReference> getAllUserCourseReferences();

    @Query(name = "findCourseByLessonId", nativeQuery = true)
    CourseEntity findCourseByLessonId(@Param("lessonId") final Long lessonId);

    @Query(name = "getCoursePrincipleIdByCourseId", nativeQuery = true)
    Long findPrincipleIdByCourseId(@Param("courseId") final Long courseId);

    @Query(name = "getCoursePrincipleIdByLessonId", nativeQuery = true)
    Long findPrincipleIdByLessonId(@Param("lessonId") final Long lessonId);

    @Modifying
    @Query(name = "bindUserToCourseQuery", nativeQuery = true)
    void bindUserToCourse(@Param("userId") final Long userId,
                          @Param("courseId") final Long courseId,
                          @Param("status") final Integer status,
                          @Param("lessonIndex") final Integer lessonIndex);


    @Modifying
    @Query(name = "updateUserAuthoritiesForCourse", nativeQuery = true)
    void updateUserAuthoritiesForCourse(@Param("userId") final Long userId,
                                        @Param("courseId") final Long courseId,
                                        @Param("authorities") final String authorities);

    @Modifying
    @Query(name = "assignCourseAuthoritiesToUser", nativeQuery = true)
    void assignCourseAuthoritiesToUser(@Param("courseId") final Long courseId,
                                       @Param("userId") final Long userId,
                                       @Param("authorities") final String authorityList);

    @Modifying
    @Query(value = " UPDATE management.app_user_course_reference " +
            " SET lesson_index = :lessonIndex WHERE app_user_id = :userId AND course_id = :courseId", nativeQuery = true)
    void progressCourseLessonIndex(@Param("userId") final Long userId,
                                   @Param("courseId") final Long courseId,
                                   @Param("lessonIndex") final Integer lessonIndex);

    @Modifying
    @Query(value = " UPDATE management.app_user_course_reference " +
            " SET status = :#{#status.getCode()} WHERE app_user_id = :userId AND course_id = :courseId", nativeQuery = true)
    void updateProgressStatus(@Param("userId") final Long userId,
                              @Param("courseId") final Long courseId,
                              @Param("status") final CourseProgressStatus status);



    @Query(value = "SELECT aucr.status FROM management.app_user_course_reference aucr WHERE aucr.app_user_id = :userId AND aucr.course_id = :courseId", nativeQuery = true)
    CourseProgressStatus getCourseProgressStatus(final Long userId, final Long courseId);


    @Query(value = "SELECT * FROM management.course c WHERE c.description LIKE :description", nativeQuery = true)
    Set<CourseEntity> searchByDescription(@Param("description") final String description);

    @Query(value = "SELECT * FROM management.course c WHERE c.subject LIKE :subject", nativeQuery = true)
    Set<CourseEntity> searchBySubject(@Param("subject") final String subject);

    @Query(value = "SELECT * FROM management.course c WHERE c.course_principal = :principleId", nativeQuery = true)
    Set<CourseEntity> searchByPrinciple(@Param("principleId") final Long principleId);

}
