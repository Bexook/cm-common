package com.cm.common.security.management.access.impl;

import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.dto.LessonDTO;
import com.cm.common.model.enumeration.CourseAuthorities;
import com.cm.common.model.enumeration.MediaType;
import com.cm.common.model.enumeration.UserRole;
import com.cm.common.security.AppUserDetails;
import com.cm.common.security.management.access.UserAccessValidation;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.lesson.LessonService;
import com.cm.common.service.user.AppUserService;
import com.cm.common.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("userAccessValidation")
public class UserAccessValidationImpl implements UserAccessValidation {

    private final AppUserService appUserService;
    private final CourseService courseService;
    private final LessonService lessonService;

    @Override
    public boolean isAnonymous() {
        return Objects.isNull(getCurrentAppUser());
    }

    @Override
    public boolean isAdminAllowedFlag(final boolean flag) {
        return getCurrentAppUser().getUserRole() == UserRole.ADMIN || flag;
    }

    @Override
    public boolean isCourseTeacher(final Long courseId) {
        final AppUserDetails appUser = getCurrentAppUser();
        final List<Long> courseTeacherId = appUserService.getCourseUsersByCourseIdAndUserRole(courseId, UserRole.TEACHER).stream()
                .map(AppUserDTO::getId)
                .collect(Collectors.toList());
        return appUser.getUserRole() == UserRole.TEACHER && courseTeacherId.contains(appUser.getUserId());
    }

    @Override
    public boolean isCourseTeacherByLessonId(final Long lessonId) {
        final AppUserDetails appUser = getCurrentAppUser();
        final LessonDTO lesson = lessonService.getLessonData(lessonId);
        final boolean courseTeacher = isCourseTeacher(lesson.getCourse().getId());
        return appUser.getUserRole() == UserRole.TEACHER && courseTeacher;
    }

    @Override
    public boolean hasAuthoritiesForCourse(final Long courseId, final String authority) {
        final AppUserDetails loggedUser = getCurrentAppUser();
        return appUserService.getUserAuthorityForCourse(loggedUser.getUserId(), courseId).contains(CourseAuthorities.valueOf(authority));
    }

    @Override
    public boolean hasAuthoritiesForCourseByLessonId(final Long lessonId, final String authority) {
        final AppUserDetails loggedUser = getCurrentAppUser();
        final Long courseId = courseService.getCourseByLessonId(lessonId).getId();
        return appUserService.getUserAuthorityForCourse(loggedUser.getUserId(), courseId).contains(CourseAuthorities.valueOf(authority));
    }

    @Override
    public boolean isAdmin() {
        final AppUserDetails loggedUser = getCurrentAppUser();
        return Objects.nonNull(loggedUser) && loggedUser.getUserRole().equals(UserRole.ADMIN);
    }

    @Override
    public boolean managementAccess(final Long courseId) {
        return isCourseTeacher(courseId) || isAdmin() || isCoursePrinciple(courseId);
    }

    @Override
    public boolean isTeacher() {
        final AppUserDetails loggedUser = getCurrentAppUser();
        return loggedUser.getUserRole() == UserRole.TEACHER;
    }

    @Override
    public boolean isCoursePrinciple(final Long courseId) {
        final AppUserDetails loggedUser = getCurrentAppUser();
        return Objects.equals(courseService.getCoursePrincipleId(courseId), loggedUser.getUserId());
    }

    @Override
    public boolean isCoursePrincipleByLessonId(final Long lessonId) {
        final AppUserDetails loggedUser = getCurrentAppUser();
        return Objects.equals(courseService.getCoursePrincipleIdByLessonId(lessonId), loggedUser.getUserId());
    }


    @Override
    public boolean isCurrentUser(final Long userId) {
        return Objects.equals(getCurrentAppUser().getUserId(), userId);
    }

    @Override
    public boolean isCurrentUser(final String email) {
        return Objects.equals(getCurrentAppUser().getUsername(), email);
    }

    @Override
    public boolean scheduledJob() {
        final AppUserDetails currentUser = getCurrentAppUser();
        return Objects.equals(currentUser.getUsername(), "SCHEDULED_JOB") && currentUser.getUserRole() == UserRole.SCHEDULED_JOB;
    }

    @Override
    public boolean onlyHomeworkUpload(final MediaType type) {
        return !(getCurrentAppUser().getUserRole() == UserRole.STUDENT && type != MediaType.PDF_HOMEWORK);
    }

    private AppUserDetails getCurrentAppUser() {
        final Object user = AuthorizationUtil.getCurrentUser();
        return user instanceof String ? null : ((AppUserDetails) user);
    }


}
