package com.cm.common.controller;

import com.cm.common.exception.SystemException;
import com.cm.common.model.dto.CourseDTO;
import com.cm.common.model.dto.CourseOverviewDTO;
import com.cm.common.model.enumeration.CourseAuthorities;
import com.cm.common.model.enumeration.CourseSearchCriteria;
import com.cm.common.service.course.CourseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseManagementResource {


    private final CourseService courseService;


    @GetMapping("/edit/{courseId}")
    public ResponseEntity<CourseDTO> getCourseData(@PathVariable("courseId") final Long courseId) {
        return ResponseEntity.ok().body(courseService.getCourseById(courseId));
    }

    @PostMapping("/create")
    @PreAuthorize("@userAccessValidation.isAdmin() || @userAccessValidation.isTeacher()")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody final CourseDTO courseDTO) {
        return ResponseEntity.ok().body(courseService.createCourse(courseDTO));
    }

    @PostMapping("/delete")
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public void deleteCourseById(@RequestParam("courseId") final Long courseId) {
        courseService.deleteCourseById(courseId);
    }

    @PostMapping("/update")
    public ResponseEntity<CourseDTO> updateCourse(@RequestBody final CourseDTO courseDTO) {
        return ResponseEntity.ok().body(courseService.updateCourse(courseDTO));
    }

    @GetMapping("/get/{courseId}")
    public ResponseEntity<CourseOverviewDTO> getCourseDataOverview(@PathVariable("courseId") final Long courseId) {
        return ResponseEntity.ok().body(courseService.getCourseOverviewById(courseId));
    }

    @PostMapping("/user/register")
    public void registerUserToCourse(@RequestParam("courseId") final Long courseId) {
        courseService.registerStudentUserToCourse(courseId);
    }

    @GetMapping("/list/overview")
    public ResponseEntity<List<CourseOverviewDTO>> getCoursesOverview(@RequestParam(value = "available", defaultValue = "true") final boolean isAvailable) {
        return ResponseEntity.ok().body(courseService.getCoursesOverview(isAvailable));
    }

    @PostMapping("/search")
    public ResponseEntity<Set<CourseOverviewDTO>> searchByCriteria(@RequestBody(required = false) Map<CourseSearchCriteria, Object> criteria) {
        return ResponseEntity.ok().body(courseService.searchByCriteria(criteria));
    }

    @PostMapping("/teacher/add")
    public void addTeacherToCourseWithAuthorities(@RequestParam("userId") final Long userId, @RequestParam("courseId") final Long courseId, @RequestBody Set<CourseAuthorities> authorities) {
        courseService.addTeacherToCourseWithAuthorities(userId, courseId, List.copyOf(authorities));
    }

    @PostMapping("/teacher/update/authority")
    public void updateTeacherAuthoritiesForCourse(@RequestParam("userId") final Long userId, @RequestParam("courseId") final Long courseId, @RequestBody Set<CourseAuthorities> authorities) {
        try {
            courseService.updateCourseAuthoritiesForTeacherById(userId, courseId, List.copyOf(authorities));
        } catch (JsonProcessingException e) {
            throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
