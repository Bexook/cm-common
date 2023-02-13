package com.cm.common.service.exam.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.ExamEvaluationEntity;
import com.cm.common.model.dto.ExamDTO;
import com.cm.common.model.dto.ExamEvaluationDTO;
import com.cm.common.model.dto.QuestionResultDTO;
import com.cm.common.model.dto.UserEvaluationResultDTO;
import com.cm.common.model.enumeration.CourseProgressStatus;
import com.cm.common.model.enumeration.ExamStatus;
import com.cm.common.repository.ExamEvaluationRepository;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.exam.ExamResultService;
import com.cm.common.service.exam.ExamService;
import com.cm.common.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamEvaluationRepository examEvaluationRepository;
    private final OrikaBeanMapper mapper;
    private final ExamService examService;
    private final CourseService courseService;


    @Override
    public void saveUserExam(final ExamEvaluationDTO examEvaluation) {
        final Integer amountOfQuestionsInExam = examService.getAmountOfQuestionsForExamById(examEvaluation.getExam().getId());
        if (examEvaluation.getUserResults().size() == amountOfQuestionsInExam) {
            examEvaluation.setExamStatus(ExamStatus.FINISHED);
        } else {
            examEvaluation.setExamStatus(ExamStatus.DRAFT);
        }
        examEvaluationRepository.save(mapper.map(examEvaluation, ExamEvaluationEntity.class));
    }

    @Override
    public Set<ExamEvaluationDTO> getAllDraftExamsForUserByCourseId(final Long courseId) {
        final ExamDTO exam = courseService.getCourseById(courseId).getExam();
        return mapper.mapAsSet(examEvaluationRepository.getAllRecordsByExamIdAndUserIdAndStatus(
                        exam.getId(),
                        AuthorizationUtil.getCurrentUserNullable().getUserId(),
                        ExamStatus.DRAFT),
                ExamEvaluationDTO.class
        );
    }

    @Override
    public UserEvaluationResultDTO evaluateExamGradeForCourse(final Long takeId) {
        final ExamEvaluationDTO results = mapper.map(examEvaluationRepository.findById(takeId), ExamEvaluationDTO.class);
        final ExamDTO exam = results.getExam();
        final Set<ExamEvaluationEntity> allDraftTakes = examEvaluationRepository.getAllRecordsByExamIdAndUserIdAndStatus(exam.getId(), AuthorizationUtil.getCurrentUserNullable().getUserId(), ExamStatus.DRAFT);
        if (results.getExamStatus() == ExamStatus.DRAFT) {
            throw new SystemException("Finish exam before evaluating results", HttpStatus.BAD_REQUEST);
        }
        final Integer userGrade = results.getUserResults().stream()
                .filter(q -> q.getUserAnswer().isRightAnswer())
                .map(QuestionResultDTO::getAmountOfPoints)
                .reduce(0, Integer::sum);
        final CourseProgressStatus userCourseStatus = exam.getMinGrade() <= userGrade ? CourseProgressStatus.CERTIFIED : CourseProgressStatus.FAILED;
        results.setExamStatus(ExamStatus.EVALUATED);
        examEvaluationRepository.save(mapper.map(results, ExamEvaluationEntity.class));
        courseService.updateCourseStatusForUserByCourseIdAndUserId(AuthorizationUtil.getCurrentUserNullable().getUserId(), exam.getCourse().getId(), userCourseStatus);
        examEvaluationRepository.deleteAll(allDraftTakes);
        return new UserEvaluationResultDTO()
                .setGrade(userGrade)
                .setExamStatus(ExamStatus.EVALUATED)
                .setCourseProgressStatus(userCourseStatus);
    }

    @Override
    public void deleteExamDraftByTakeId(final Long takeId) {
        examEvaluationRepository.deleteById(takeId);
    }
}
