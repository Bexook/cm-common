package com.cm.common.model.domain;

import com.cm.common.security.AppUserDetails;
import com.cm.common.util.AuthorizationUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Entity
@Table(schema = "management", name = "question")
public class QuestionEntity extends BaseEntity {

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "index")
    private Integer index;

    @Column(name = "amount_of_points")
    private Integer amountOfPoints;
    @ManyToOne
    @JoinColumn(name = "exam_id", referencedColumnName = "id")
    private ExamEntity exam;
    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
    @ToString.Exclude
    private List<AnswerEntity> answers;




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
