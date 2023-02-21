package com.cm.common.service.homework.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.HomeworkEntity;
import com.cm.common.model.dto.HomeworkDTO;
import com.cm.common.model.dto.MediaDTO;
import com.cm.common.model.enumeration.MediaType;
import com.cm.common.repository.HomeworkRepository;
import com.cm.common.service.homework.HomeworkService;
import com.cm.common.service.media.MediaService;
import com.cm.common.service.media.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    private final FileUploadService fileUploadService;
    private final MediaService mediaService;
    private final HomeworkRepository homeworkRepository;
    private final OrikaBeanMapper mapper;

    @Override
    @Transactional
    public void submitHomework(final HomeworkDTO homework) {
        if (!mediaService.existsById(homework.getMedia().getId()) || homework.getMedia().getMediaType() != MediaType.PDF_HOMEWORK) {
            throw new SystemException("Wrong media.", HttpStatus.BAD_REQUEST);
        }
        homeworkRepository.save(mapper.map(homework, HomeworkEntity.class));
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isTeacher()")
    public void gradeHomework(final HomeworkDTO homework) {
        final HomeworkEntity homeworkEntity = homeworkRepository.findById(homework.getId())
                .orElseThrow(() -> new SystemException("Homework does not exist", HttpStatus.BAD_REQUEST));
        homeworkEntity.setGrade(homework.getGrade());
        homeworkEntity.setTeacherNotes(homework.getTeacherNotes());
        homeworkEntity.setEvaluated(true);
        homeworkRepository.save(homeworkEntity);
    }

    @Override
    @Transactional
    public void updateHomeworkMedia(final MediaDTO media, final Long homeworkId) {
        if (mediaService.existsById(media.getId()) || media.getMediaType() != MediaType.PDF_HOMEWORK) {
            throw new SystemException("Wrong media.", HttpStatus.BAD_REQUEST);
        }
        final HomeworkEntity entity = homeworkRepository.findById(homeworkId).orElseThrow(() -> new SystemException("Homework does not exist", HttpStatus.BAD_REQUEST));
        mediaService.deleteMediaById(entity.getId());
        homeworkRepository.bindMediaToHomework(media.getId(), homeworkId);
    }

    @Override
    @Transactional
    public void deleteHomework(final Long homeworkId) {
        final HomeworkEntity entity = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new SystemException("Homework does not exist", HttpStatus.BAD_REQUEST));
        homeworkRepository.delete(entity);
        mediaService.deleteMediaById(entity.getMedia().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<HomeworkDTO> getLessonHomeworks(final Long lessonId, final boolean evaluated) {
        final Set<HomeworkDTO> homeworks = mapper.mapAsSet(homeworkRepository.getHomeworksForLessonByLessonIdAndEvaluatedFlagValue(lessonId, evaluated), HomeworkDTO.class);
        return homeworks.stream()
                .peek(h -> h.getMedia().setUrl(generateReadUrl(h.getMedia().getKey())))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer evaluateHomeworkUserGradeForCourse(final Long courseId, final Long userId) {
        return homeworkRepository.getHomeworkGradesForCourseByCourseId(courseId, userId).stream().reduce(Math::addExact).orElse(0);
    }


    private String generateReadUrl(final String key) {
        return fileUploadService.generateTemporaryLinkForReadingFile(key);
    }
}
