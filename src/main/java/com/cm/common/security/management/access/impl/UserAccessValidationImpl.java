package com.cm.common.security.management.access.impl;

import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.CourseAuthorities;
import com.cm.common.model.enumeration.UserRole;
import com.cm.common.security.AppUserDetails;
import com.cm.common.security.management.access.UserAccessValidation;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.user.AppUserService;
import com.cm.common.util.AuthorizationUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("userAccessValidation")
public class UserAccessValidationImpl implements UserAccessValidation {

    private final AppUserService appUserService;
    private final CourseService courseService;

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
        return appUser.getUserRole() == UserRole.TEACHER;
    }

    @Override
    public boolean hasAuthoritiesForCourse(final Long courseId, final String authority) throws JsonProcessingException {
        final AppUserDetails loggedUser = getCurrentAppUser();
        return appUserService.getUserAuthorityForCourse(loggedUser.getUserId(), courseId).contains(CourseAuthorities.valueOf(authority));
    }

    @Override
    public boolean hasAuthoritiesForCourseByLessonId(final Long lessonId, final String authority) throws JsonProcessingException {
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
    public boolean onlyScheduledJob() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.equals(authentication.getName(), "system") && authentication.getAuthorities().contains(new SimpleGrantedAuthority("SCHEDULED_JOB"))) {
            return true;
        }
        return false;
    }

    private AppUserDetails getCurrentAppUser() {
        return (AppUserDetails) AuthorizationUtil.getCurrentUserNullable();
    }


}
