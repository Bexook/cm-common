package com.cm.common.service.media.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.LessonEntity;
import com.cm.common.model.domain.MediaEntity;
import com.cm.common.model.dto.LessonDTO;
import com.cm.common.model.dto.MediaDTO;
import com.cm.common.model.enumeration.MediaType;
import com.cm.common.model.enumeration.MediaUploadStatus;
import com.cm.common.repository.MediaRepository;
import com.cm.common.security.AppUserDetails;
import com.cm.common.service.lesson.LessonService;
import com.cm.common.service.media.MediaService;
import com.cm.common.service.media.file.FileUploadService;
import com.cm.common.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final FileUploadService fileUploadService;
    private final LessonService lessonService;
    private final MediaRepository mediaRepository;
    private final OrikaBeanMapper mapper;

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.onlyHomeworkUpload(#type)")
    public MediaDTO upload(final Long lessonId, final MediaType type, final MultipartFile file) {
        validateFile(file);
        final String fileKey = buildFileKey(lessonId, file.getOriginalFilename());
        final LessonDTO lesson = lessonService.getLessonData(lessonId);
        final MediaDTO media = new MediaDTO()
                .setMediaType(type)
                .setKey(fileKey)
                .setLesson(lesson);
        final MediaUploadStatus uploadStatus = fileUploadService.uploadFile(file, fileKey);
        media.setUploadStatus(uploadStatus);
        final MediaEntity newMedia = mediaRepository.save(mapper.map(media, MediaEntity.class));
        return mapper.map(newMedia, MediaDTO.class);
    }


    @Override
    public MediaDTO update(final MultipartFile file, final Long mediaId) {
        final MediaEntity entity = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new SystemException("Media does not exist", HttpStatus.BAD_REQUEST));
        final String newKey = buildFileKey(entity.getLesson().getId(), file.getName());
        fileUploadService.deleteFile(entity.getKey());
        entity.setKey(newKey);
        final MediaUploadStatus uploadStatus = fileUploadService.uploadFile(file, newKey);
        entity.setUploadStatus(uploadStatus);
        mediaRepository.save(entity);
        return mapper.map(entity, MediaDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<MediaDTO> getMediaForLessonByType(final Long lessonId, final MediaType type) {
        final LessonEntity baseEntity = new LessonEntity();
        baseEntity.setId(lessonId);
        final Example<MediaEntity> example = Example.of(new MediaEntity().setMediaType(type).setLesson(baseEntity));
        final Set<MediaDTO> lessonMedia = mapper.mapAsSet(mediaRepository.findAll(example), MediaDTO.class);
        return lessonMedia.stream()
                .peek(m -> m.setUrl(fileUploadService.generateTemporaryLinkForReadingFile(m.getKey())))
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void deleteMediaById(final Long mediaId) {
        final MediaEntity entity = mediaRepository.findById(mediaId).orElseThrow(() ->
                new SystemException("Media does not exist", HttpStatus.BAD_REQUEST));
        fileUploadService.deleteFile(entity.getKey());
        mediaRepository.delete(entity);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsById(final Long mediaId) {
        return mediaRepository.existsById(mediaId);
    }

    //user email
    private void validateFile(final MultipartFile file) {
        if (file.getOriginalFilename().contains("..")) {
            throw new SystemException("Bad file name.", HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            throw new SystemException("Bad file extension. Consume only PDF", HttpStatus.BAD_REQUEST);
        }
        final AppUserDetails currentUser = (AppUserDetails) AuthorizationUtil.getCurrentUser();
        if (!file.getOriginalFilename().startsWith(currentUser.getUsername())) {
            throw new SystemException("Upload failed. Wrong file name", HttpStatus.BAD_REQUEST);
        }
    }

    private String buildFileKey(final Long lessonId, final String fileName) {
        return StringUtils.join(List.of(lessonId, fileName), "_");
    }
}
