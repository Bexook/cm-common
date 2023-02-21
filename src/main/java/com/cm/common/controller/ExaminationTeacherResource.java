package com.cm.common.controller;

import com.cm.common.model.dto.ExamDTO;
import com.cm.common.service.exam.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exam/teacher")
public class ExaminationTeacherResource {
    private final ExamService examService;

    @GetMapping("/edit/{examId}")
    public ResponseEntity<ExamDTO> getExamDTO(@PathVariable("examId") final Long examId) {
        return ResponseEntity.ok().body(examService.getExamById(examId));
    }

    @PostMapping("/create")
    public ResponseEntity<ExamDTO> createExamFoeCourse(@RequestParam("courseId") final Long courseId, @RequestBody ExamDTO exam) {
        return ResponseEntity.ok().body(examService.createExamForCourse(exam, courseId));
    }

    @PostMapping("/update")
    public ResponseEntity<ExamDTO> updateExam(@RequestBody final ExamDTO exam) {
        return ResponseEntity.ok().body(examService.updateExam(exam));
    }

    @PostMapping("/delete")
    public void deleteExam(@RequestParam("examId") final Long examId) {
        examService.deleteExam(examId);
    }

}
