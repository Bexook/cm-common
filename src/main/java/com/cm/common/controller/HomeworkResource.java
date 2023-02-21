package com.cm.common.controller;

import com.cm.common.model.dto.HomeworkDTO;
import com.cm.common.model.dto.MediaDTO;
import com.cm.common.service.homework.HomeworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/homework")
public class HomeworkResource {

    private final HomeworkService homeworkService;


    @PostMapping("/submit")
    public void submitHomework(@RequestBody final HomeworkDTO homework) {
        homeworkService.submitHomework(homework);
    }

    @PostMapping("/update/{homeworkId}")
    public void update(@PathVariable final Long homeworkId, @RequestBody final MediaDTO media) {
        homeworkService.updateHomeworkMedia(media, homeworkId);
    }

    @PostMapping("/grade")
    public void gradeHomework(@RequestBody final HomeworkDTO homework) {
        homeworkService.gradeHomework(homework);
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<Set<HomeworkDTO>> getHomeworksForLesson(@PathVariable("lessonId") final Long lessonId,
                                                                  @RequestParam("evaluated") final boolean evaluated) {
        return ResponseEntity.ok().body(homeworkService.getLessonHomeworks(lessonId, evaluated));
    }


    @PostMapping("/delete")
    public void deleteHomework(@RequestParam("homeworkId") final Long homeworkId) {
        homeworkService.deleteHomework(homeworkId);
    }


}
