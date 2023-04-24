package com.cm.common.controller;

import com.cm.common.model.dto.ExamDTO;
import com.cm.common.model.dto.ExamEvaluationDTO;
import com.cm.common.model.dto.UserEvaluationResultDTO;
import com.cm.common.service.exam.ExamEvaluationService;
import com.cm.common.service.exam.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exam/student")
public class ExaminationStudentResource {

    private final ExamEvaluationService examEvaluationService;
    private final ExamService examService;

    @GetMapping("/draft/all")
    public ResponseEntity<Set<ExamEvaluationDTO>> getAllDraftExamsForUser(@RequestParam("courseId") final Long courseId) {
        return ResponseEntity.ok().body(examEvaluationService.getAllDraftExamsForUserByCourseId(courseId));
    }

    @GetMapping("/")
    public ResponseEntity<ExamDTO> getExam(@RequestParam("courseId") final Long courseId) {
        return ResponseEntity.ok().body(examService.getExamDataForCourse(courseId));
    }

    @PostMapping("/answer")
    public ExamEvaluationDTO saveAnswers(@RequestBody final ExamEvaluationDTO examAnswers) {
        return examEvaluationService.saveUserExam(examAnswers);
    }

    @PutMapping("/submit")
    public ResponseEntity<UserEvaluationResultDTO> submitUserExam(@RequestParam("takeId") final Long takeId) {
        return ResponseEntity.ok().body(examEvaluationService.evaluateExamGradeForCourse(takeId));
    }

    @DeleteMapping("/delete")
    public void deleteTakeById(@RequestParam("takeId") final Long takeId) {
        examEvaluationService.deleteExamDraftByTakeId(takeId);
    }
}
