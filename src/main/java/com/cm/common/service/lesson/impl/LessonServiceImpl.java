package com.cm.common.service.lesson.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.LessonEntity;
import com.cm.common.model.dto.LessonDTO;
import com.cm.common.repository.LessonRepository;
import com.cm.common.security.AppUserDetails;
import com.cm.common.service.lesson.LessonService;
import com.cm.common.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final OrikaBeanMapper mapper;
    private final LessonRepository lessonRepository;


    @Override
    @Transactional(readOnly = true)
    public Integer getUserCourseProgressIndex(final Long courseId) {
        final AppUserDetails userDetails = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        return lessonRepository.getCurrentLessonIndex(userDetails.getUserId(), courseId);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.hasAuthoritiesForCourse(#courseId, 'CREATE_LESSONS') || @userAccessValidation.isAdmin() || @userAccessValidation.isCoursePrinciple(#courseId)")
    public LessonDTO createLesson(final Long courseId, final LessonDTO lessonDTO) {
        if (lessonRepository.existsBySubject(lessonDTO.getSubject())) {
            throw new SystemException("Lesson with given subject already exists try update lesson or create new one with unique subject", HttpStatus.BAD_REQUEST);
        }
        final LessonDTO savedLesson = mapper.map(lessonRepository.save(mapper.map(lessonDTO, LessonEntity.class)), LessonDTO.class);
        lessonRepository.bindLessonToCourse(savedLesson.getId(), courseId);
        return savedLesson;
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isCoursePrincipleByLessonId(#lesson.id) || @userAccessValidation.hasAuthoritiesForCourseByLessonId(#lesson.id, 'UPDATE_LESSONS')")
    public LessonDTO updateLesson(final LessonDTO lesson) {
        return mapper.map(lessonRepository.save(mapper.map(lesson, LessonEntity.class)), LessonDTO.class);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isCoursePrincipleByLessonId(#lessonId) || @userAccessValidation.isAdmin()")
    public void deleteLessonById(final Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.isAdmin() " +
            "|| @userAccessValidation.isCoursePrincipleByLessonId(#lessonId)")
    public LessonDTO getLessonData(final Long lessonId) {
        final LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new SystemException("Lesson does not exist", HttpStatus.BAD_REQUEST));
        return mapper.map(lesson, LessonDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.isAdmin() " +
            "|| @userAccessValidation.isCoursePrinciple(#courseId) " +
            "|| @userAccessValidation.isCourseTeacher(#courseId)")
    public Set<LessonDTO> findAllLessonsForCourse(final Long courseId) {
        return Set.copyOf(mapper.mapAsList(lessonRepository.findAll(), LessonDTO.class));
    }


    @Override
    public Set<LessonDTO> getAvailableForUserLessons(final Long courseId) {
        final Set<LessonDTO> allCourseLessons = findAllLessonsForCourse(courseId);
        final Integer userProgress = getUserCourseProgressIndex(courseId);
        return allCourseLessons.stream()
                .filter(l -> l.getIndex() <= userProgress)
                .collect(Collectors.toSet());
    }

    @Override
    public Integer calculateLessonAmountForCourse(final Long courseId) {
        return lessonRepository.countByCourseId(courseId);
    }

    @Override
    public boolean existsLessonBySubject(final String subject) {
        return lessonRepository.existsBySubject(subject);
    }
}
