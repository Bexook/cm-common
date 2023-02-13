package com.cm.common.service.exam.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.ExamEntity;
import com.cm.common.model.dto.ExamDTO;
import com.cm.common.model.enumeration.CourseProgressStatus;
import com.cm.common.repository.ExamRepository;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.exam.ExamService;
import com.cm.common.service.lesson.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.cm.common.constant.ApplicationValidationConstants.NO_EXAMINATION_COURSE_STATUSES;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final OrikaBeanMapper mapper;
    private final ExamRepository examRepository;
    private final CourseService courseService;


    @Override
    @Transactional(readOnly = true)
    public ExamDTO getExamById(final Long examId) {
        final ExamEntity exam = examRepository.findById(examId).orElseThrow(() -> {
            log.error("Exam with id: {} does not exist", examId);
            throw new SystemException("Exam does not exist", HttpStatus.BAD_REQUEST);
        });
        return mapper.map(exam, ExamDTO.class);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public void deleteExam(final Long examId) {
        examRepository.deleteById(examId);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isCourseTeacher(#courseId)  || @userAccessValidation.isCoursePrinciple(#courseId)  || @userAccessValidation.isAdmin()")
    public ExamDTO createExamForCourse(final ExamDTO exam, final Long courseId) {
        final ExamDTO savedExamData = mapper.map(examRepository.save(mapper.map(exam, ExamEntity.class)), ExamDTO.class);
        examRepository.bindExamToCourse(savedExamData.getId(), courseId);
        return savedExamData;
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isCourseTeacher(#exam.course.id) " +
            " || @userAccessValidation.isCoursePrinciple(#exam.course.id) " +
            " || @userAccessValidation.isAdmin()")
    public ExamDTO updateExam(final ExamDTO exam) {
        if (!examRepository.existsById(exam.getId())) {
            log.error("Exam with id: {}  does not exist", exam.getId());
            throw new SystemException("Exam does not exist", HttpStatus.BAD_REQUEST);
        }
        return mapper.map(examRepository.save(mapper.map(exam, ExamEntity.class)), ExamDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.hasAuthoritiesForCourse(#courseId, 'READ_COURSE') || @userAccessValidation.isAdmin()")
    public ExamDTO getExamDataForCourse(final Long courseId) {
        final CourseProgressStatus userProgressStatus = courseService.getUserCourseProgressStatus(courseId);
        if (NO_EXAMINATION_COURSE_STATUSES.contains(userProgressStatus)) {
            throw new SystemException("Not user did not finish the course or need admin approval for retesting", HttpStatus.FORBIDDEN);
        }
        return mapper.map(examRepository.findByCourseId(courseId), ExamDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAmountOfQuestionsForExamById(final Long examId) {
        return examRepository.countQuestionsForExam(examId);
    }
}
