package com.cm.common.model.domain;

import javax.persistence.*;

@Entity
@Table(schema = "management", name = "answer")
public class AnswerEntity extends BaseEntity {

    @Column(name = "answer_value")
    private String answerValue;
    @Column(name = "right_answer")
    private boolean rightAnswer;

    @ManyToOne
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private QuestionEntity question;

}
