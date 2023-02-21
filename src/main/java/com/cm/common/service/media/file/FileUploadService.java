package com.cm.common.service.media.file;

import com.cm.common.model.enumeration.MediaUploadStatus;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    MediaUploadStatus uploadFile(final MultipartFile file, final String key);

    String generateTemporaryLinkForReadingFile(final String key);

    void deleteFile(final String key);


}
