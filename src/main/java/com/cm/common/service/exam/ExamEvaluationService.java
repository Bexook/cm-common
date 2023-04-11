package com.cm.common.service.exam;

import com.cm.common.model.dto.ExamEvaluationDTO;
import com.cm.common.model.dto.UserEvaluationResultDTO;

import java.util.Set;

public interface ExamEvaluationService {

    Set<ExamEvaluationDTO> getAllDraftExamsForUserByCourseId(final Long courseId);

    ExamEvaluationDTO saveUserExam(final ExamEvaluationDTO exam);

    UserEvaluationResultDTO evaluateExamGradeForCourse(final Long takeId);

    void deleteExamDraftByTakeId(final Long takeId);


}
