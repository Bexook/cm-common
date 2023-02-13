package com.cm.common.constant;

import com.cm.common.model.enumeration.CourseProgressStatus;

import java.util.Set;

public class ApplicationValidationConstants {
    //Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String EMAIL_REGEX = "^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}.[a-z]{2,}$";
    public static final String USER_NAMES_REGEX = "^([A-Z][a-z]{2,30})$";
    public static final String LESSON_INDEX_REGEX = "^(?:[0-9]|[1-4][0-9])$";

    public static final String GENERAL_TEXT_REGEX = "^([A-Za-z0-9\\s_%$=+-№!&*#]*)";

    public static final Set<CourseProgressStatus> NO_EXAMINATION_COURSE_STATUSES = Set.of(CourseProgressStatus.ASSIGNED, CourseProgressStatus.IN_PROGRESS, CourseProgressStatus.CERTIFIED, CourseProgressStatus.FAILED);


}
