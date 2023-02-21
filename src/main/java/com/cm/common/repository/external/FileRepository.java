package com.cm.common.repository.external;

import com.cm.common.model.enumeration.MediaUploadStatus;
import org.springframework.web.multipart.MultipartFile;

public interface FileRepository {

    MediaUploadStatus uploadFile(final MultipartFile file, final String key);

    String generateTemporaryLinkForReadingFile(final String key);

    byte[] downloadFile(final String key);

    void deleteFile(final String key);

}
