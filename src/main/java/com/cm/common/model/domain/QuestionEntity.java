package com.cm.common.model.domain;

import javax.persistence.*;
import java.util.List;

@Entity(name = "question")
@Table(schema = "management")
public class QuestionEntity extends BaseEntity {

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "amount_of_points")
    private Integer amountOfPoints;

    @Column(name = "index")
    private Integer index;

    @ManyToOne
    @JoinColumn(name = "exam_id", referencedColumnName = "id")
    private ExamEntity exam;
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<AnswerEntity> answers;


}
