package com.cm.common.model.domain;

import com.cm.common.security.AppUserDetails;
import com.cm.common.util.AuthorizationUtil;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
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
