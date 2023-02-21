package com.cm.common.model.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@NamedNativeQueries(value = {
        @NamedNativeQuery(name = "getHomeworksForLessonByLessonIdAndEvaluatedFlagValue", query = "SELECT * FROM management.homework hw " +
                " WHERE hw.id IN (SELECT hw.id FROM management.homework hw " +
                " LEFT JOIN management.media m ON m.id = hw.media_id " +
                " LEFT JOIN management.lesson l on l.id = m.lesson_id " +
                " WHERE hw.evaluated = :evaluated AND l.id = :lessonId)"),
        @NamedNativeQuery(name = "getCountedHomeworkGradeForAllLessonsWithEvaluatedFlagTrueAndCourseId", query = "SELECT hw.grade " +
                " FROM management.homework hw " +
                " WHERE hw.id IN (SELECT hw.id " +
                "                FROM management.homework hw " +
                "                         LEFT JOIN management.media m ON m.id = hw.media_id " +
                "                         LEFT JOIN management.lesson l on l.id = m.lesson_id " +
                "                         LEFT JOIN management.course c on l.course_id = c.id " +
                "                WHERE c.id = :courseId " +
                "                  AND hw.app_user_id = :userId " +
                "                  AND hw.evaluated = true)")

})

@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "homework", schema = "management")
public class HomeworkEntity extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "app_user_id", referencedColumnName = "id")
    private AppUserEntity appUser;
    @OneToOne
    @JoinColumn(name = "media_id", referencedColumnName = "id")
    private MediaEntity media;
    @Column(name = "grade")
    private Integer grade;
    @Column(name = "teacher_notes")
    private String teacherNotes;
    @Column(name = "evaluated")
    private boolean evaluated;

}
