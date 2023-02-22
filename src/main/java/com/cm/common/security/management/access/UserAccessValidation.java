package com.cm.common.security.management.access;

import com.cm.common.model.enumeration.MediaType;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserAccessValidation {

    boolean isAdminAllowedFlag(final boolean isAvailable);

    boolean isAnonymous();

    boolean isCourseTeacherByLessonId(final Long lessonId);

    boolean isCourseTeacher(final Long courseId);

    boolean isCoursePrincipleByLessonId(final Long lessonId);

    boolean hasAuthoritiesForCourse(final Long course, String authority) throws JsonProcessingException;

    boolean isAdmin();

    boolean managementAccess(final Long courseId);

    boolean isTeacher();

    boolean isCoursePrinciple(final Long courseId);

    boolean hasAuthoritiesForCourseByLessonId(final Long lessonId, final String authority) throws JsonProcessingException;

    boolean isCurrentUser(final Long userId);

    boolean isCurrentUser(final String email);

    boolean scheduledJob();

    boolean onlyHomeworkUpload(final MediaType type);

}
