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
        @NamedNativeQuery(name = "bindUserToCourseQuery",
                query = " INSERT INTO management.app_user_course_reference " +
                        " VALUES(:userId, :courseId, :status, :lessonIndex)"),
        @NamedNativeQuery(name = "assignCourseAuthoritiesToUser",
                query = " INSERT INTO management.course_user_authority " +
                        " VALUES(:courseId, :userId, :authorities)"),
        @NamedNativeQuery(name = "getCoursePrincipleIdByCourseId", query = "SELECT au.id FROM management.course c " +
                " LEFT JOIN management.app_user au ON course_principal=au.id " +
                " WHERE c.id = :courseId "),
        @NamedNativeQuery(name = "getCoursePrincipleIdByLessonId", query = "SELECT au.id FROM management.lesson l " +
                " LEFT JOIN management.course c ON l.course_id = c.id " +
                " JOIN management.app_user au ON c.course_principal = au.id" +
                " WHERE l.id = :lessonId "),
        @NamedNativeQuery(name = "findCourseByLessonId", query = "SELECT * FROM management.course c " +
                " LEFT JOIN management.lesson l ON l.course_id = c.id " +
                " WHERE l.id = :lessonId"),
        @NamedNativeQuery(name = "updateUserAuthoritiesForCourse", query = " UPDATE management.course_user_authority cua " +
                " SET cua.authorities = :authority " +
                " WHERE cua.user_id = :userId AND cua.course_id = :courseId"),
        @NamedNativeQuery(name = "getAllUserCourseReferences",
                query = "SELECT * FROM management.app_user_course_reference",
                resultSetMapping = "getAllUserCourseReferencesMapping")
})

@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "getAllUserCourseReferencesMapping",
                classes = @ConstructorResult(targetClass = UserCourseReference.class, columns = {
                        @ColumnResult(name = "app_user_id", type = Long.class),
                        @ColumnResult(name = "course_id", type = Long.class),
                        @ColumnResult(name = "lesson_index", type = Integer.class),
                        @ColumnResult(name = "status", type = Integer.class)
                }))
})

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(schema = "management", name = "course")
public class CourseEntity extends BaseEntity {

    @Column(name = "subject")
    private String subject;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private boolean available;
    @Column(name = "amount_of_points")
    private Integer amountOfPoints;
    @ManyToOne(targetEntity = AppUserEntity.class)
    @JoinColumn(name = "course_principal", referencedColumnName = "id")
    private AppUserEntity coursePrinciple;
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<LessonEntity> lessons;
    @OneToOne(mappedBy = "course")
    private ExamEntity exam;

    @PrePersist
    void prePersistChild() {
        final AppUserDetails userDetails = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        if (Objects.isNull(coursePrinciple)) {
            coursePrinciple = userDetails.getAppUserEntity();
        }
        if (Objects.isNull(this.getCreatedBy())) {
            this.setCreatedBy(userDetails.getAppUserEntity());
        }
        if (Objects.isNull(this.getCreatedDate())) {
            this.setCreatedDate(LocalDateTime.now());
        }
    }


}
