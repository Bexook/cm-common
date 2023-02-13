package com.cm.common.service.lesson;

import com.cm.common.model.dto.LessonDTO;

import java.util.Set;

public interface LessonService {

    LessonDTO createLesson(final Long courseId, final LessonDTO lessonDTO);

    LessonDTO updateLesson(final LessonDTO lesson);

    void deleteLessonById(final Long lessonId);

    LessonDTO getLessonData(final Long lessonId);

    Set<LessonDTO> findAllLessonsForCourse(final Long courseId);

    Integer calculateLessonAmountForCourse(final Long courseId);

    Set<LessonDTO> getAvailableForUserLessons(final Long courseId);


}
