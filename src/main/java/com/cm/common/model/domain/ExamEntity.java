package com.cm.common.model.domain;

import javax.persistence.*;
import java.util.List;


@NamedNativeQueries(value = {
        @NamedNativeQuery(name = "bindExamToCourse",
                query = " UPDATE management.exam e " +
                        " SET e.course_id = :courseId " +
                        " WHERE e.id = :examId"),
        @NamedNativeQuery(name = "findByCourseId",
                query = " SELECT * FROM management.exam e " +
                        " WHERE e.course_id = :courseId"),
        @NamedNativeQuery(name = "countQuestionsForExam", query = "SELECT COUNT(e.id) FROM management.exam e " +
                " LEFT JOIN management.question q ON q.exam_id = e.id " +
                " WHERE e.id=:examId ")
})

@Entity(name = "exam")
@Table(schema = "management")
public class ExamEntity extends BaseEntity {
    @Column(name = "min_grade")
    private Integer minGrade;
    @Column(name = "max_grade")
    private Integer maxGrade;
    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private CourseEntity course;
    @OneToMany(mappedBy = "exam", fetch = FetchType.LAZY)
    private List<QuestionEntity> questions;

}
