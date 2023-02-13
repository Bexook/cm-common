package com.cm.common.controller;


import com.cm.common.model.dto.LessonDTO;
import com.cm.common.service.lesson.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson")
public class LessonResource {

    private final LessonService lessonService;


    @PostMapping("/create")
    public ResponseEntity<LessonDTO> createLesson(@RequestParam("courseId") final Long courseId,
                                                  @Valid @RequestBody final LessonDTO lesson) {
        return ResponseEntity.ok().body(lessonService.createLesson(courseId, lesson));
    }

    @PostMapping("/delete")
    public void deleteLesson(@RequestParam("lessonId") final Long lessonId) {
        lessonService.deleteLessonById(lessonId);
    }

    @PostMapping("/update")
    @PreAuthorize("@userAccessValidation.isAdmin() || @userAccessValidation.isTeacher()")
    public ResponseEntity<LessonDTO> updateLesson(@RequestBody final LessonDTO lesson) {
        return ResponseEntity.ok().body(lessonService.updateLesson(lesson));
    }

    @GetMapping("/edit/{lessonId}")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable("lessonId") final Long lessonId) {
        return ResponseEntity.ok().body(lessonService.getLessonData(lessonId));
    }

    @GetMapping("/available/all")
    public ResponseEntity<Set<LessonDTO>> getAllAvailableLessons(@RequestParam("courseId") final Long courseId) {
        return ResponseEntity.ok().body(lessonService.getAvailableForUserLessons(courseId));
    }

    @GetMapping("/all")
    public ResponseEntity<Set<LessonDTO>> getAllLessonForCourse(@RequestParam("courseId") final Long courseId) {
        return ResponseEntity.ok().body(lessonService.findAllLessonsForCourse(courseId));
    }
}
