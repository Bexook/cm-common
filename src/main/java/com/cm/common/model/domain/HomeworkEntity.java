package com.cm.common.model.domain;

import com.cm.common.security.AppUserDetails;
import com.cm.common.util.AuthorizationUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@NamedNativeQueries(value = {
        @NamedNativeQuery(name = "getHomeworksForLessonByLessonIdAndEvaluatedFlagValue", query = "SELECT * FROM management.homework hw " +
                " WHERE hw.id IN (SELECT hw.id FROM management.homework hw " +
                " LEFT JOIN management.media m ON m.id = hw.media_id " +
                " LEFT JOIN management.lesson l on l.id = m.lesson_id " +
                " WHERE hw.evaluated = :evaluated AND l.id = :lessonId)", resultClass = HomeworkEntity.class),
        @NamedNativeQuery(name = "getCountedHomeworkGradeForAllLessonsWithEvaluatedFlagTrueAndCourseId", query = "SELECT hw.grade " +
                " FROM management.homework hw " +
                " WHERE hw.id IN (SELECT hw.id " +
                "                FROM management.homework hw " +
                "                         LEFT JOIN management.media m ON m.id = hw.media_id " +
                "                         LEFT JOIN management.lesson l on l.id = m.lesson_id " +
                "                         LEFT JOIN management.course c on l.course_id = c.id " +
                "                WHERE c.id = :courseId " +
                "                  AND hw.created_by = :userId " +
                "                  AND hw.evaluated = true)")

})

@Data
@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "homework", schema = "management")
public class HomeworkEntity extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", referencedColumnName = "id")
    private MediaEntity media;
    @Column(name = "grade")
    private Integer grade;
    @Column(name = "teacher_notes")
    private String teacherNotes;
    @Column(name = "evaluated")
    private boolean evaluated;


    @PrePersist
    void prePersistChild() {
        final AppUserDetails userDetails = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        if (Objects.isNull(this.getCreatedBy())) {
            this.setCreatedBy(userDetails.getAppUserEntity());
        }
        if (Objects.isNull(this.getCreatedDate())) {
            this.setCreatedDate(LocalDateTime.now());
        }
    }
}
