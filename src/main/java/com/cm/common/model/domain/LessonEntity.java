package com.cm.common.model.domain;

import com.cm.common.security.AppUserDetails;
import com.cm.common.util.AuthorizationUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@RequiredArgsConstructor
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


    @PrePersist
    void prePersist() {
        if (Objects.isNull(this.getCreatedBy())) {
            final AppUserDetails userDetails = (AppUserDetails) AuthorizationUtil.getCurrentUser();
            this.setCreatedBy(userDetails.getAppUserEntity());
        }
        if (Objects.isNull(this.getCreatedDate())) {
            this.setCreatedDate(LocalDateTime.now());
        }
    }
}
