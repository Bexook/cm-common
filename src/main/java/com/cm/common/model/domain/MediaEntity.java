package com.cm.common.model.domain;

import com.cm.common.adapter.MediaTypeAdapter;
import com.cm.common.model.enumeration.MediaType;

import javax.persistence.*;

@Entity
@Table(schema = "management", name = "media")
public class MediaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "media_type")
    @Enumerated(value = EnumType.ORDINAL)
    @Convert(converter = MediaTypeAdapter.class)
    private MediaType mediaType;

    @Column(name = "key")
    private String key;

    @ManyToOne
    @JoinColumn(name = "lesson_id", referencedColumnName = "id")
    private LessonEntity lesson;

}
