//package com.cm.common.repository.external.impl;
//
//import com.cm.common.exception.SystemException;
//import com.cm.common.model.enumeration.MediaUploadStatus;
//import com.cm.common.repository.external.FileRepository;
//import com.cm.common.security.AppUserDetails;
//import com.cm.common.util.AuthorizationUtil;
//import com.google.auth.oauth2.ServiceAccountCredentials;
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Bucket;
//import com.google.cloud.storage.Storage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Repository;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class FileRepositoryImpl implements FileRepository {
//
////    private final Bucket homeworkBucket;
//    private final Storage storage;
//
//    @Value("${google.cloud.credentials-json-path}")
//    private String credentialsJsonPath;
//
//    @Override
//    public MediaUploadStatus uploadFile(final MultipartFile file, final String key) {
//        final AppUserDetails currentUser = (AppUserDetails) AuthorizationUtil.getCurrentUser();
//        try {
////            homeworkBucket.create(key, file.getInputStream());
//            log.info("Homework uploaded for user: {}", currentUser.getUsername());
//            return MediaUploadStatus.SUCCESS;
//        } catch (IOException e) {
//            log.error("File upload failed: {}, username: {}\n Exception message: {}", file.getName(), currentUser.getUsername(), e.getMessage());
//            return MediaUploadStatus.FAILED;
//        }
//    }
//
//    @Override
//    public String generateTemporaryLinkForReadingFile(final String key) {
//        try {
////            final URL signedUrl = storage.signUrl(BlobInfo.newBuilder(homeworkBucket.getName(), key).build(),
//                    1, TimeUnit.DAYS, Storage.SignUrlOption.signWith(ServiceAccountCredentials.fromStream(new ClassPathResource(credentialsJsonPath).getInputStream())));
//            return signedUrl.toString();
//        } catch (final IOException e) {
//            log.error("Not able to sign url for file: {}", key);
//            throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @Override
//    public byte[] downloadFile(final String key) {
//        return homeworkBucket.get(key).getContent();
//    }
//
//
//    @Override
//    public void deleteFile(final String key) {
//        try {
//            storage.delete(BlobId.of(homeworkBucket.getName(), key));
//        } catch (RuntimeException e) {
//            log.error("File {} was not deleted from external storage", key);
//            throw new SystemException("File deletion failed", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//}
