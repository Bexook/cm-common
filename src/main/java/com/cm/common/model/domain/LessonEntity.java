package com.cm.common.model.domain;

import com.cm.common.util.AuthorizationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NamedNativeQueries(value = {
        @NamedNativeQuery(name = "getAllLessonIdsForCourseId", query = "SELECT l.id FROM management.lesson l WHERE l.course_id = :courseId")
})
@Entity
@Table(schema = "management", name = "lesson")
public class LessonEntity extends BaseEntity {

    @Column(name = "subject")
    private String subject;
    @Column(name = "lesson_text")
    private String text;
    @Column(name = "index")
    private Integer index;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private CourseEntity course;

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    private List<MediaEntity> media;


    @PrePersist
    void prePersist() {
        if (Objects.isNull(this.getCreatedBy())) {
            this.setCreatedBy(AuthorizationUtil.getCurrentUserNullable().getAppUserEntity());
        }
        if (Objects.isNull(this.getCreatedDate())) {
            this.setCreatedDate(LocalDateTime.now());
        }
    }
}
