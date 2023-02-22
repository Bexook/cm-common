package com.cm.common.model.domain;

import com.cm.common.adapter.MediaTypeAdapter;
import com.cm.common.model.enumeration.MediaType;
import com.cm.common.model.enumeration.MediaUploadStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Table(schema = "management", name = "media")
public class MediaEntity {

    @Id
    @Column(name = "id", columnDefinition = "SERIAL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "media_type")
    @Enumerated(value = EnumType.ORDINAL)
    @Convert(converter = MediaTypeAdapter.class)
    private MediaType mediaType;
    @Column(name = "key")
    private String key;
    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status")
    private MediaUploadStatus uploadStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private LessonEntity lesson;

}
