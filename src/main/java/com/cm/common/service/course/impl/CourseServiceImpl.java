package com.cm.common.service.course.impl;

import com.cm.common.classifiers.SearchCriteria;
import com.cm.common.constant.ApplicationConstants;
import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.CourseEntity;
import com.cm.common.model.domain.UserCourseReference;
import com.cm.common.model.dto.*;
import com.cm.common.model.enumeration.*;
import com.cm.common.repository.CourseRepository;
import com.cm.common.security.AppUserDetails;
import com.cm.common.service.SearchCriteriaExecutor;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.exam.ExamService;
import com.cm.common.service.lesson.LessonService;
import com.cm.common.service.user.AppUserService;
import com.cm.common.util.AuthorizationUtil;
import com.cm.common.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
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

    @Value("${service.course.user-available-amount:5}")
    private Integer userAvailableAmount;
    private final OrikaBeanMapper mapper;
    private final CourseRepository courseRepository;
    private final AppUserService appUserService;

    private final ExamService examService;
    private final LessonService lessonService;

    private final Map<SearchCriteria, SearchCriteriaExecutor<CourseOverviewDTO>> searchCriteriaExecutorMap = new HashMap<>();

    {
        searchCriteriaExecutorMap.put(CourseSearchCriteria.BY_COURSE_PRINCIPAL, this::searchByCoursePrinciple);
        searchCriteriaExecutorMap.put(CourseSearchCriteria.BY_DESCRIPTION, this::searchByDescription);
        searchCriteriaExecutorMap.put(CourseSearchCriteria.BY_SUBJECT, this::searchBySubject);
    }


    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAdmin() || @userAccessValidation.isCoursePrinciple(#courseId)")
    public void updateCourseAvailabilityStatus(final Long courseId, final boolean status) {
        final CourseEntity entity = courseRepository.findById(courseId).orElseThrow(() -> new SystemException("Course does not exist", HttpStatus.BAD_REQUEST));
        if (Objects.isNull(entity.getAmountOfPoints()) || (Objects.nonNull(entity.getAmountOfPoints()) && entity.getAmountOfPoints() < 50)) {
            throw new SystemException("Please specify amount of points bigger or equal to 50", HttpStatus.BAD_REQUEST);
        }
        courseRepository.updateCourseAvailabilityStatus(courseId, status);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.isCoursePrinciple(#courseId) ||  @userAccessValidation.isCourseTeacher(#courseId)" +
            " || @userAccessValidation.isAdmin()")
    public CourseDTO getCourseById(final Long courseId) {
        final CourseEntity course = courseRepository.findById(courseId).orElseThrow(() -> new SystemException("Course does not exist", HttpStatus.BAD_REQUEST));
        return mapper.map(course, CourseDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.hasAuthoritiesForCourse(#courseId, 'READ_COURSE') || @userAccessValidation.managementAccess(#courseId)")
    public CourseOverviewDTO getCourseOverviewById(final Long courseId) {
        final CourseEntity searchExample = new CourseEntity().setAvailable(true);
        searchExample.setId(courseId);
        final CourseEntity course = courseRepository.findOne(Example.of(searchExample)).
                orElseThrow(() -> new SystemException("Course does not exist", HttpStatus.BAD_REQUEST));
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
        final AppUserDTO principle = courseDTO.getCoursePrinciple();
        final AppUserDetails currentUser = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        validateCoursePrinciple(principle, currentUser);
        if (Objects.nonNull(principle)) {
            final AppUserDTO appUserDTO = appUserService.getUserById(principle.getId());
            if (appUserDTO.getUserRole() == UserRole.ADMIN) {
                throw new SystemException("Admin can not be course principle, please specify course principle", HttpStatus.BAD_REQUEST);
            }
            courseDTO.setCoursePrinciple(appUserDTO);
            log.info("Creating course with principle: {}", appUserDTO.getEmail());
            log.info("Created by {}", currentUser.getUsername());
        } else {
            courseDTO.setCoursePrinciple(appUserService.getUserById(currentUser.getUserId()));
            log.info("Created by {}", currentUser.getUsername());
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
        final AppUserDetails currentUser = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        validateUserCourseReferencing(courseId, currentUser.getUserId());
        //For student exists only default authority -> READ_COURSE
        final String serializedAuthorities = JsonUtils.serialize(List.of(CourseAuthorities.READ_COURSE.getAuthority()));
        // 0 lesson is introduction lesson to course
        courseRepository.bindUserToCourse(currentUser.getUserId(), courseId, CourseProgressStatus.ASSIGNED.getCode(), 0);
        courseRepository.assignCourseAuthoritiesToUser(courseId, currentUser.getUserId(), serializedAuthorities);
    }


    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public void registerStudentUserToCourseByAdmin(final Long userId, final Long courseId) {
        validateUserCourseReferencing(courseId, userId);
        //For student exists only default authority -> READ_COURSE
        final String serializedAuthorities = JsonUtils.serialize(List.of(CourseAuthorities.READ_COURSE.getAuthority()));
        // 0 lesson is introduction lesson to course
        courseRepository.bindUserToCourse(userId, courseId, CourseProgressStatus.ASSIGNED.getCode(), 0);
        courseRepository.assignCourseAuthoritiesToUser(courseId, userId, serializedAuthorities);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isCoursePrinciple(#courseId) || @userAccessValidation.isAdmin()")
    public void addTeacherToCourseWithAuthorities(final Long userId,
                                                  final Long courseId,
                                                  final List<CourseAuthorities> authorities) {
        //Only user with role TEACHER could be assigned to course. ADMIN has access to all courses by default.
        final AppUserDTO appUser = appUserService.getUserById(userId);
        if (appUser.getUserRole() == UserRole.STUDENT || appUser.getUserRole() == UserRole.ADMIN) {
            throw new SystemException("User does not contain required role", HttpStatus.BAD_REQUEST);
        }
        final String serializedAuthorities = JsonUtils.serialize(authorities);
        courseRepository.bindUserToCourse(userId, courseId, CourseProgressStatus.ASSIGNED.getCode(), 0);
        courseRepository.assignCourseAuthoritiesToUser(courseId, userId, serializedAuthorities);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isCoursePrinciple(#courseId) || @userAccessValidation.isAdmin()")
    public void updateCourseAuthoritiesForTeacherById(final Long userId, final Long courseId, final List<CourseAuthorities> authorities) {
        validateTeacherLinkedToCourse(userId, courseId);
        final AppUserDTO appUser = appUserService.getUserById(userId);
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
    @PreAuthorize("@userAccessValidation.scheduledJob() || @userAccessValidation.isAdmin()")
    public ScheduledJobReportDTO progressCourseLessonIndex() {
        final AppUserDetails userDetails = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        final Set<UserCourseReference> userCourseReference = courseRepository.getAllUserCourseReferences();
        final ScheduledJobReportDTO report = new ScheduledJobReportDTO()
                .setImpactedAccountsCount(userCourseReference.size())
                .setStartBy(userDetails.getUsername())
                .setName(ApplicationConstants.LESSON_PROGRESS_JOB_NAME);
        try {
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
            report.setStatus(JobStatus.SUCCEEDED);
        } catch (final RuntimeException e) {
            report.setFailureReason(e.getCause().getMessage());
            report.setStatus(JobStatus.FAILED);
        }
        return report;
    }


    @Override
    @Transactional(readOnly = true)
    public CourseProgressStatus getUserCourseProgressStatus(final Long courseId) {
        final AppUserDetails userDetails = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        return courseRepository.getCourseProgressStatus(userDetails.getUserId(), courseId);
    }

    @Override
    public void updateCourseStatusForUserByCourseIdAndUserId(final Long userId, final Long courseId, final CourseProgressStatus courseProgressStatus) {
        if (!courseRepository.existsById(courseId)) {
            throw new SystemException("Course does not exist", HttpStatus.BAD_REQUEST);
        }
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

    private void validateUserCourseReferencing(final Long courseId, final Long userId) {
        final CourseEntity example = new CourseEntity();
        example.setId(courseId);
        example.setAvailable(true);
        final boolean courseExistsById = courseRepository.exists(Example.of(example));
        final Integer numberOfUserCoursesInProgress = courseRepository.countUserCoursesByStatus(userId, courseId, CourseProgressStatus.IN_PROGRESS);
        final boolean alreadyRegistered = courseRepository.isUserAlreadyRegisteredToCourse(userId, courseId);
        if (alreadyRegistered) {
            throw new SystemException("User already registered", HttpStatus.BAD_REQUEST);
        }
        if (numberOfUserCoursesInProgress >= userAvailableAmount) {
            throw new SystemException("Acceded available amount of courses", HttpStatus.BAD_REQUEST);
        }
        if (!courseExistsById) {
            throw new SystemException("Course does not exist", HttpStatus.BAD_REQUEST);
        }
    }

    private Set<CourseOverviewDTO> searchByCoursePrinciple(final Object subject) {
        return courseRepository.searchByPrinciple(Long.parseLong((String) subject)).stream()
                .map(this::mapToCourseOverviewDTO)
                .collect(Collectors.toSet());
    }

    private void validateTeacherLinkedToCourse(final Long userId, final Long courseId) {
        final AppUserDTO user = appUserService.getUserById(userId);
        if (user.getUserRole() != UserRole.TEACHER && !courseRepository.isUserAlreadyRegisteredToCourse(userId, courseId)) {
            throw new SystemException("Teacher is not linked to course", HttpStatus.BAD_REQUEST);
        }

    }

    private static void validateCoursePrinciple(final AppUserDTO principle, final AppUserDetails currentUser) {
        if ((Objects.isNull(principle) || Objects.isNull(principle.getId())) && currentUser.getUserRole() == UserRole.ADMIN) {
            throw new SystemException("Please specify course principle id", HttpStatus.BAD_REQUEST);
        }
        if (Objects.nonNull(principle) && (Objects.equals(principle.getId(), currentUser.getUserId()) && currentUser.getUserRole() == UserRole.ADMIN)) {
            throw new SystemException("Admin can not be course principle, please specify course principle", HttpStatus.BAD_REQUEST);
        }
        if (currentUser.getUserRole() == UserRole.TEACHER && Objects.nonNull(principle)) {
            throw new SystemException("Teacher is only capable of creating courses for himself. Ask system administrator to create create course for somebody else", HttpStatus.BAD_REQUEST);
        }
    }
}
