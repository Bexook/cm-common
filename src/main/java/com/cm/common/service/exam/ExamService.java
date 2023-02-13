package com.cm.common.service.exam;

import com.cm.common.model.dto.ExamDTO;

public interface ExamService {

    ExamDTO getExamById(final Long examId);

    ExamDTO updateExam(final ExamDTO exam);

    void deleteExam(final Long examId);

    ExamDTO createExamForCourse(final ExamDTO exam, final Long courseId);

    ExamDTO getExamDataForCourse(final Long courseId);

    Integer getAmountOfQuestionsForExamById(final Long examId);


}
