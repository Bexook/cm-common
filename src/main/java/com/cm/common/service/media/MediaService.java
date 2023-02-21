package com.cm.common.service.media;

import com.cm.common.model.dto.MediaDTO;
import com.cm.common.model.enumeration.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface MediaService {

    boolean existsById(final Long mediaId);


    MediaDTO update(final MultipartFile file, final Long mediaId);

    MediaDTO upload(final Long lessonId, final MediaType type, final MultipartFile file);

    Set<MediaDTO> getMediaForLessonByType(final Long lessonId, final MediaType type);

    void deleteMediaById(final Long mediaId);
}
