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
import com.cm.common.security.AppUserDetails;
import com.cm.common.service.course.CourseService;
import com.cm.common.service.exam.ExamEvaluationService;
import com.cm.common.service.exam.ExamService;
import com.cm.common.service.homework.HomeworkService;
import com.cm.common.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamEvaluationServiceImpl implements ExamEvaluationService {

    private final Integer percentsOfPointsToPassEvaluation = 80;
    private final HomeworkService homeworkService;
    private final ExamEvaluationRepository examEvaluationRepository;
    private final OrikaBeanMapper mapper;
    private final ExamService examService;
    private final CourseService courseService;


    @Override
    public ExamEvaluationDTO saveUserExam(final ExamEvaluationDTO examEvaluation) {
        final Integer amountOfQuestionsInExam = examService.getAmountOfQuestionsForExamById(examEvaluation.getExam().getId());
        if (examEvaluation.getUserResults().size() == amountOfQuestionsInExam) {
            examEvaluation.setExamStatus(ExamStatus.FINISHED);
        } else {
            examEvaluation.setExamStatus(ExamStatus.DRAFT);
        }
        return mapper.map(examEvaluationRepository.save(mapper.map(examEvaluation, ExamEvaluationEntity.class)), ExamEvaluationDTO.class);
    }

    @Override
    public Set<ExamEvaluationDTO> getAllDraftExamsForUserByCourseId(final Long courseId) {
        final AppUserDetails userDetails = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        final ExamDTO exam = courseService.getCourseOverviewById(courseId).getExam();
        return mapper.mapAsSet(examEvaluationRepository.getAllRecordsByExamIdAndUserIdAndStatus(
                        exam.getId(),
                        userDetails.getUserId(),
                        ExamStatus.DRAFT),
                ExamEvaluationDTO.class
        );
    }

    @Override
    public UserEvaluationResultDTO evaluateExamGradeForCourse(final Long takeId) {
        final AppUserDetails userDetails = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        final ExamEvaluationDTO results = mapper.map(examEvaluationRepository.findById(takeId), ExamEvaluationDTO.class);
        final ExamDTO exam = results.getExam();
        final Integer courseAmountOfPoints = exam.getCourse().getAmountOfPoints();
        final Integer homeworkGrade = homeworkService.evaluateHomeworkUserGradeForCourse(exam.getCourse().getId(), userDetails.getUserId());
        final Set<ExamEvaluationEntity> allDraftTakes = examEvaluationRepository.getAllRecordsByExamIdAndUserIdAndStatus(exam.getId(), userDetails.getUserId(), ExamStatus.DRAFT);
        if (results.getExamStatus() == ExamStatus.DRAFT) {
            throw new SystemException("Finish exam before evaluating results", HttpStatus.BAD_REQUEST);
        }
        final Integer examGrade = results.getUserResults().stream()
                .filter(q -> q.getUserAnswer().isRightAnswer())
                .map(QuestionResultDTO::getAmountOfPoints)
                .reduce(0, Integer::sum);
        if (examGrade < exam.getMinGrade()) {
            examEvaluationRepository.save(mapper.map(results, ExamEvaluationEntity.class));
            courseService.updateCourseStatusForUserByCourseIdAndUserId(userDetails.getUserId(), exam.getCourse().getId(), CourseProgressStatus.FAILED);
            return new UserEvaluationResultDTO()
                    .setGrade(examGrade)
                    .setExamStatus(ExamStatus.EVALUATED)
                    .setCourseProgressStatus(CourseProgressStatus.FAILED);
        }
        final CourseProgressStatus userCourseStatus = ((homeworkGrade + examGrade) / courseAmountOfPoints) * 100 > percentsOfPointsToPassEvaluation ? CourseProgressStatus.CERTIFIED : CourseProgressStatus.FAILED;
        results.setExamStatus(ExamStatus.EVALUATED);
        examEvaluationRepository.save(mapper.map(results, ExamEvaluationEntity.class));
        courseService.updateCourseStatusForUserByCourseIdAndUserId(userDetails.getUserId(), exam.getCourse().getId(), userCourseStatus);
        examEvaluationRepository.deleteAll(allDraftTakes);
        return new UserEvaluationResultDTO()
                .setGrade(examGrade)
                .setExamStatus(ExamStatus.EVALUATED)
                .setCourseProgressStatus(userCourseStatus);
    }

    @Override
    public void deleteExamDraftByTakeId(final Long takeId) {
        examEvaluationRepository.deleteById(takeId);
    }
}
