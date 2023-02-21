package com.cm.common.service.media.file.impl;

import com.cm.common.exception.SystemException;
import com.cm.common.model.enumeration.MediaUploadStatus;
import com.cm.common.repository.external.FileRepository;
import com.cm.common.service.media.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileRepository fileRepository;
    private final Path localStoragePath = Path.of("upload_failed_files");


    @Override
    public MediaUploadStatus uploadFile(final MultipartFile file, final String key) {
        final MediaUploadStatus uploadStatus = fileRepository.uploadFile(file, key);
        if (uploadStatus == MediaUploadStatus.FAILED) {
            log.warn("File upload to external storage failed. Moving to local storage.");
            moveToLocalStorage(file, key);
            log.warn("Moved to local storage.");
            return MediaUploadStatus.LOCAL_STORAGE;
        }
        return uploadStatus;
    }

    @Override
    public String generateTemporaryLinkForReadingFile(final String key) {
        return fileRepository.generateTemporaryLinkForReadingFile(key);
    }


    @Override
    public void deleteFile(final String key) {
        fileRepository.deleteFile(key);
    }

    private void moveToLocalStorage(final MultipartFile file, final String key) {
        try {
            final InputStream inputStream = file.getResource().getInputStream();
            Files.copy(inputStream, localStoragePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            log.error("File was not moved local storage. Filename: {}, FileKey: {}", file.getName(), key);
            throw new SystemException("File was not moved local storage.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
