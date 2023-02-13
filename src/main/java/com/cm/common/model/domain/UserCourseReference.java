package com.cm.common.model.domain;


import com.cm.common.model.enumeration.CourseProgressStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserCourseReference {

    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("courseId")
    private Long courseId;
    @JsonProperty("lessonIndex")
    private Integer lessonIndex;
    @JsonProperty("status")
    private CourseProgressStatus status;


    public UserCourseReference(final Long userId,
                               final Long courseId,
                               final Integer lessonIndex,
                               final Integer status) {
        this.userId = userId;
        this.courseId = courseId;
        this.lessonIndex = lessonIndex;
        this.status = CourseProgressStatus.getByCode(status);
    }
}
