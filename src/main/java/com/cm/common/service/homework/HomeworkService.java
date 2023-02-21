package com.cm.common.service.homework;

import com.cm.common.model.dto.HomeworkDTO;
import com.cm.common.model.dto.MediaDTO;

import java.util.Set;

public interface HomeworkService {

    void submitHomework(final HomeworkDTO homework);

    void gradeHomework(final HomeworkDTO homework);

    void updateHomeworkMedia(final MediaDTO media, final Long homeworkId);

    void deleteHomework(final Long homeworkId);

    Set<HomeworkDTO> getLessonHomeworks(final Long lessonId, final boolean evaluated);

    Integer evaluateHomeworkUserGradeForCourse(final Long courseId, final Long userId);
}
