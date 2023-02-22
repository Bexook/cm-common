package com.cm.common.model.domain;

import com.cm.common.security.AppUserDetails;
import com.cm.common.util.AuthorizationUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@NamedNativeQueries(value = {
        @NamedNativeQuery(name = "bindExamToCourse",
                query = " UPDATE management.exam  " +
                        " SET course_id = :courseId " +
                        " WHERE id = :examId"),
        @NamedNativeQuery(name = "findByCourseId",
                query = " SELECT * FROM management.exam e " +
                        " WHERE e.course_id = :courseId"),
        @NamedNativeQuery(name = "countQuestionsForExam", query = "SELECT COUNT(e.id) FROM management.exam e " +
                " LEFT JOIN management.question q ON q.exam_id = e.id " +
                " WHERE e.id=:examId ")
})

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(schema = "management", name = "exam")
public class ExamEntity extends BaseEntity {
    @Column(name = "min_grade")
    private Integer minGrade;
    @Column(name = "max_grade")
    private Integer maxGrade;
    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private CourseEntity course;
    @OneToMany(mappedBy = "exam", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<QuestionEntity> questions;


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
