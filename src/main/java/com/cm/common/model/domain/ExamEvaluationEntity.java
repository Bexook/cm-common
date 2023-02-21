package com.cm.common.model.domain;

import com.cm.common.adapter.QuestionResultsAdapter;
import com.cm.common.model.dto.QuestionResultDTO;
import com.cm.common.model.enumeration.ExamStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@Accessors(chain = true)
@Table(name = "exam_result", schema = "management")
@Entity(name = "exam_result")
public class ExamEvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne(targetEntity = AppUserEntity.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUserEntity appUser;
    @JoinColumn(name = "exam_id", referencedColumnName = "id")
    @ManyToOne(targetEntity = ExamEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = false)
    private ExamEntity exam;
    @Convert(converter = QuestionResultsAdapter.class)
    @Column(name = "answers_json")
    private Set<QuestionResultDTO> answersJson;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private ExamStatus status;
    @Column(name = "teacher_note")
    private String teacherNote;

}
