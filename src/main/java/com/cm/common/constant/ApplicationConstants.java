package com.cm.common.constant;

import com.cm.common.model.enumeration.CourseProgressStatus;

import java.util.Set;

public class ApplicationConstants {
    //Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character
    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String EMAIL_REGEX = "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String USER_NAMES_REGEX = "^([A-Z][a-z]{2,30})$";
    public static final String LESSON_INDEX_REGEX = "^(?:[0-9]|[1-4][0-9])$";

    public static final String GENERAL_TEXT_REGEX = "^([A-Za-z0-9\\s_%$=+-№!&*#]*)";

    public static final Set<CourseProgressStatus> NO_EXAMINATION_COURSE_STATUSES = Set.of(CourseProgressStatus.ASSIGNED, CourseProgressStatus.IN_PROGRESS, CourseProgressStatus.CERTIFIED, CourseProgressStatus.FAILED);

    public static final String ACCOUNT_DELETION_JOB_NAME = "ACCOUNT_DELETION_JOB";
    public static final String ACCOUNT_DELETION_WARNING_JOB_NAME = "ACCOUNT_DELETION_WARNING_JOB";
    public static final String LESSON_PROGRESS_JOB_NAME = "LESSON_PROGRESS_JOB";
}
