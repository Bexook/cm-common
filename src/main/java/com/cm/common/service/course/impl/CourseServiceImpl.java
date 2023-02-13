package com.cm.common.service.course.impl;

import com.cm.common.classifiers.SearchCriteria;
import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.UserCourseReference;
import com.cm.common.model.domain.CourseEntity;
import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.dto.CourseDTO;
import com.cm.common.model.dto.CourseOverviewDTO;
import com.cm.common.model.enumeration.CourseAuthorities;
import com.cm.common.model.enumeration.CourseProgressStatus;
import com.cm.common.model.enumeration.CourseSearchCriteria;
import com.cm.common.model.enumeration.UserRole;
import com.cm.common.repository.CourseRepository;
import com.cm.common.security.AppUserDetails;
import com.cm.common.service.SearchCriteriaExecutor;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.lesson.LessonService;
import com.cm.common.service.user.AppUserService;
import com.cm.common.util.AuthorizationUtil;
import com.cm.common.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.MapUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.cm.common.util.SqlUtils.wrapToSqlLikeStatement;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final OrikaBeanMapper mapper;
    private final CourseRepository courseRepository;
    private final AppUserService appUserService;
    @Autowired
    private LessonService lessonService;

    private final Map<SearchCriteria, SearchCriteriaExecutor<CourseOverviewDTO>> searchCriteriaExecutorMap = new HashMap<>();

    {
        searchCriteriaExecutorMap.put(CourseSearchCriteria.BY_COURSE_PRINCIPAL, this::searchByCoursePrinciple);
        searchCriteriaExecutorMap.put(CourseSearchCriteria.BY_DESCRIPTION, this::searchByDescription);
        searchCriteriaExecutorMap.put(CourseSearchCriteria.BY_SUBJECT, this::searchBySubject);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.hasAuthoritiesForCourse(#courseId, 'READ_COURSE') " +
            " || @userAccessValidation.isCoursePrinciple(#courseId) ||  @userAccessValidation.isCourseTeacher(#courseId)" +
            " || @userAccessValidation.isAdmin()")
    public CourseDTO getCourseById(final Long courseId) {
        final CourseEntity course = courseRepository.findById(courseId).orElseThrow(() -> new SystemException("Course does not exist", HttpStatus.BAD_REQUEST));
        return mapper.map(course, CourseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.hasAuthoritiesForCourse(#courseId, 'READ_COURSE') " +
            " || @userAccessValidation.isCoursePrinciple(#courseId) ||  @userAccessValidation.isCourseTeacher(#courseId)" +
            " || @userAccessValidation.isAdmin()")
    public CourseOverviewDTO getCourseOverviewById(final Long courseId) {
        final CourseEntity course = courseRepository.findById(courseId).orElseThrow(() -> new SystemException("Course does not exist", HttpStatus.BAD_REQUEST));
        return mapToCourseOverviewDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseByLessonId(final Long lessonId) {
        return mapper.map(courseRepository.findCourseByLessonId(lessonId), CourseDTO.class);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAdmin() || @userAccessValidation.isTeacher()")
    public CourseDTO createCourse(final CourseDTO courseDTO) {
        final Long principleId = courseDTO.getCoursePrinciple().getId();
        final AppUserDetails currentUser = AuthorizationUtil.getCurrentUserNullable();
        if (Objects.isNull(principleId) && currentUser.getUserRole() == UserRole.ADMIN) {
            throw new SystemException("Admin can not be course principle, please specify course principle id", HttpStatus.BAD_REQUEST);
        }
        if (Objects.nonNull(principleId) && currentUser.getUserRole() == UserRole.ADMIN) {
            final AppUserDTO appUserDTO = appUserService.getUserById(principleId);
            courseDTO.setCoursePrinciple(appUserDTO);
            log.info("Creating course with principle: {}", appUserDTO.getEmail());
            log.info("Created by {}", currentUser.getUsername());
        }
        if (Objects.nonNull(principleId) &&
                !Objects.equals(principleId, currentUser.getUserId()) &&
                currentUser.getUserRole() == UserRole.TEACHER) {
            throw new SystemException("Teacher is only capable of creating courses for himself. Ask system administrator to create create course for somebody else", HttpStatus.BAD_REQUEST);
        }
        final CourseEntity newCourse = mapper.map(courseDTO, CourseEntity.class);
        return mapper.map(courseRepository.save(newCourse), CourseDTO.class);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAdmin() || @userAccessValidation.hasAuthoritiesForCourse(#course.id, 'UPDATE_COURSE') || @userAccessValidation.isCourseTeacher(#course.id)")
    public CourseDTO updateCourse(final CourseDTO course) {
        if (Objects.isNull(course.getId()) || !courseRepository.existsById(course.getId())) {
            throw new SystemException("Course does not exist ", HttpStatus.BAD_REQUEST);
        }
        final CourseEntity courseToUpdate = mapper.map(course, CourseEntity.class);
        return mapper.map(courseRepository.save(courseToUpdate), CourseDTO.class);
    }

    @Override
    @Transactional
    public void registerStudentUserToCourse(final Long courseId) {
        final AppUserDetails currentUser = AuthorizationUtil.getCurrentUserNullable();
        //For student exists only default authority -> READ_COURSE
        final String serializedAuthorities = JsonUtils.serialize(List.of(CourseAuthorities.READ_COURSE.getAuthority()));
        // 0 lesson is introduction lesson to course
        //TODO validate is user already registered to course
        courseRepository.bindUserToCourse(currentUser.getUserId(), courseId, CourseProgressStatus.ASSIGNED.getCode(), 0);
        courseRepository.assignCourseAuthoritiesToUser(currentUser.getUserId(), courseId, serializedAuthorities);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public void registerStudentUserToCourseByAdmin(final Long userId, final Long courseId) {
        //For student exists only default authority -> READ_COURSE
        final String serializedAuthorities = JsonUtils.serialize(List.of(CourseAuthorities.READ_COURSE.getAuthority()));
        // 0 lesson is introduction lesson to course
        courseRepository.bindUserToCourse(userId, courseId, CourseProgressStatus.ASSIGNED.getCode(), 0);
        courseRepository.assignCourseAuthoritiesToUser(userId, courseId, serializedAuthorities);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isCoursePrinciple(#courseId) || @userAccessValidation.isAdmin()")
    public void addTeacherToCourseWithAuthorities(final Long userId,
                                                  final Long courseId,
                                                  final List<CourseAuthorities> authorities) throws JsonProcessingException {
        //Only user with role TEACHER could be assigned to course. ADMIN has access to all courses by default.
        final AppUserDTO appUser = appUserService.getUserById(courseId);
        if (appUser.getUserRole() == UserRole.STUDENT || appUser.getUserRole() == UserRole.ADMIN) {
            throw new SystemException("User does not contain required role", HttpStatus.BAD_REQUEST);
        }
        final String serializedAuthorities = JsonUtils.serialize(authorities);
        courseRepository.bindUserToCourse(userId, courseId, CourseProgressStatus.ASSIGNED.getCode(), 0);
        courseRepository.assignCourseAuthoritiesToUser(userId, courseId, serializedAuthorities);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isCoursePrinciple(#courseId) || @userAccessValidation.isAdmin()")
    public void updateCourseAuthoritiesForTeacherById(final Long userId, final Long courseId, final List<CourseAuthorities> authorities) throws JsonProcessingException {
        final AppUserDTO appUser = appUserService.getUserById(courseId);
        if (appUser.getUserRole() == UserRole.STUDENT || appUser.getUserRole() == UserRole.ADMIN) {
            throw new SystemException("User does not contain required role", HttpStatus.BAD_REQUEST);
        }
        final String serializedAuthorities = JsonUtils.serialize(authorities);
        courseRepository.updateUserAuthoritiesForCourse(userId, courseId, serializedAuthorities);
    }


    @Override
    @Transactional(readOnly = true)
    public Long getCoursePrincipleId(final Long courseId) {
        return courseRepository.findPrincipleIdByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.isAdminAllowedFlag(#isAvailable)")
    public List<CourseOverviewDTO> getCoursesOverview(final boolean isAvailable) {
        final List<CourseDTO> courses = mapper.mapAsList(courseRepository.findAllByAvailable(isAvailable), CourseDTO.class);
        return courses.stream()
                .map(this::mapToCourseOverviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCoursePrincipleIdByLessonId(final Long lessonId) {
        return courseRepository.findPrincipleIdByLessonId(lessonId);
    }

    @Override
    @Transactional
    public void deleteCourseById(final Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new SystemException("Course does not exist", HttpStatus.BAD_REQUEST);
        }
        courseRepository.deleteById(courseId);
    }


    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.onlyScheduledJob()")
    public void progressCourseLessonIndex() {
        final Set<UserCourseReference> userCourseReference = courseRepository.getAllUserCourseReferences();
        for (final UserCourseReference reference : userCourseReference) {
            if (isCourseLessonsFinished(reference.getStatus())) {
                continue;
            }
            reference.setLessonIndex(incrementLessonIndex(reference.getLessonIndex()));
            //change status from assigned to in_progress
            reference.setStatus(reference.getLessonIndex() == 1 ? CourseProgressStatus.IN_PROGRESS : reference.getStatus());
            final Integer lessonAmount = lessonService.calculateLessonAmountForCourse(reference.getCourseId());
            courseRepository.progressCourseLessonIndex(reference.getUserId(), reference.getCourseId(), reference.getLessonIndex());

            if (lessonAmount.equals(reference.getLessonIndex())) {
                courseRepository.updateProgressStatus(reference.getUserId(), reference.getCourseId(), CourseProgressStatus.EVALUATION_EXPECTED);
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Integer getUserCourseProgressIndex(final Long courseId) {
        return courseRepository.getCurrentLessonIndex(AuthorizationUtil.getCurrentUserNullable().getUserId(), courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseProgressStatus getUserCourseProgressStatus(final Long courseId) {
        return courseRepository.getCourseProgressStatus(AuthorizationUtil.getCurrentUserNullable().getUserId(), courseId);
    }

    @Override
    public void updateCourseStatusForUserByCourseIdAndUserId(final Long userId, final Long courseId, final CourseProgressStatus courseProgressStatus) {
        courseRepository.updateProgressStatus(userId, courseId, courseProgressStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<CourseOverviewDTO> searchByCriteria(final Map<? extends SearchCriteria, Object> criteria) {
        if (MapUtils.isEmpty(criteria)) {
            return courseRepository.findAll().stream()
                    .filter(CourseEntity::isAvailable)
                    .map(this::mapToCourseOverviewDTO)
                    .collect(Collectors.toSet());
        }
        return searchByCriteria(criteria, searchCriteriaExecutorMap);
    }

    private CourseOverviewDTO mapToCourseOverviewDTO(final CourseDTO course) {
        final Integer lessonAmount = lessonService.calculateLessonAmountForCourse(course.getId());
        return new CourseOverviewDTO()
                .setId(course.getId())
                .setSubject(course.getSubject())
                .setLessonAmount(lessonAmount)
                .setDescription(course.getDescription());
    }

    private CourseOverviewDTO mapToCourseOverviewDTO(final CourseEntity course) {
        final Integer lessonAmount = lessonService.calculateLessonAmountForCourse(course.getId());
        return new CourseOverviewDTO()
                .setId(course.getId())
                .setSubject(course.getSubject())
                .setLessonAmount(lessonAmount)
                .setDescription(course.getDescription());
    }


    boolean isCourseLessonsFinished(final CourseProgressStatus userStatus) {
        return userStatus == CourseProgressStatus.CERTIFIED ||
                userStatus == CourseProgressStatus.AWAITING_RETEST ||
                userStatus == CourseProgressStatus.FAILED ||
                userStatus == CourseProgressStatus.EVALUATION_EXPECTED;
    }

    private Integer incrementLessonIndex(final Integer currentIndex) {
        return Math.incrementExact(currentIndex);
    }

    private Set<CourseOverviewDTO> searchByDescription(final Object description) {
        return courseRepository.searchByDescription(wrapToSqlLikeStatement((String) description)).stream()
                .map(this::mapToCourseOverviewDTO)
                .collect(Collectors.toSet());
    }


    private Set<CourseOverviewDTO> searchBySubject(final Object subject) {
        return courseRepository.searchBySubject(wrapToSqlLikeStatement((String) subject)).stream()
                .map(this::mapToCourseOverviewDTO)
                .collect(Collectors.toSet());
    }

    private Set<CourseOverviewDTO> searchByCoursePrinciple(final Object subject) {
        return courseRepository.searchByPrinciple(Long.parseLong((String) subject)).stream()
                .map(this::mapToCourseOverviewDTO)
                .collect(Collectors.toSet());
    }
}
