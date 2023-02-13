package com.cm.common.model.dto;

import com.cm.common.model.enumeration.CourseProgressStatus;
import lombok.Data;

@Data
public class UserCourseResultsDTO {

    private Integer grade;
    private CourseProgressStatus status;

}
