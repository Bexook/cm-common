package com.cm.common.model.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Table(schema = "management")
@Entity(name = "app_user_test_reference")
public class AppUserExamReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(targetEntity = AppUserEntity.class)
    @JoinColumn(name = "app_user_id", referencedColumnName = "id")
    private AppUserEntity appUser;
    @ManyToOne(targetEntity = CourseEntity.class)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private CourseEntity course;
    @Column(name = "grade")
    private Integer grade;
    @Column(name = "evaluation_date")
    private LocalDate evaluationDate;


}
