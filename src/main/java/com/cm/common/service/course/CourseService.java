package com.cm.common.service.course;

import com.cm.common.classifiers.SearchCriteria;
import com.cm.common.classifiers.Searchable;
import com.cm.common.model.dto.CourseDTO;
import com.cm.common.model.dto.CourseOverviewDTO;
import com.cm.common.model.dto.ScheduledJobReportDTO;
import com.cm.common.model.enumeration.CourseAuthorities;
import com.cm.common.model.enumeration.CourseProgressStatus;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CourseService extends Searchable {


    void updateCourseAvailabilityStatus(final Long courseId, final boolean status);

    CourseOverviewDTO getCourseOverviewById(final Long courseId);

    CourseDTO getCourseById(final Long courseId);

    ScheduledJobReportDTO progressCourseLessonIndex();

    void deleteCourseById(final Long courseId);

    CourseDTO createCourse(final CourseDTO courseDTO);

    CourseDTO updateCourse(final CourseDTO courseDTO);

    void registerStudentUserToCourse(final Long courseId);

    void registerStudentUserToCourseByAdmin(final Long userId, final Long courseId);

    void addTeacherToCourseWithAuthorities(final Long userId, final Long courseId, final List<CourseAuthorities> authorities);

    void updateCourseAuthoritiesForTeacherById(final Long userId, final Long courseId, final List<CourseAuthorities> authorities) throws JsonProcessingException;

    void updateCourseStatusForUserByCourseIdAndUserId(final Long userId, final Long courseId, final CourseProgressStatus courseProgressStatus);

    Long getCoursePrincipleId(final Long courseId);

    CourseDTO getCourseByLessonId(final Long lessonId);

    Long getCoursePrincipleIdByLessonId(final Long lessonId);

    List<CourseOverviewDTO> getCoursesOverview(final boolean isAvailable);


    CourseProgressStatus getUserCourseProgressStatus(final Long courseId);

    Set<CourseOverviewDTO> searchByCriteria(final Map<? extends SearchCriteria, Object> criteria);

}
