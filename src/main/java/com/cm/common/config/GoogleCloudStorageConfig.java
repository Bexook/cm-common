package com.cm.common.config;

import com.cm.common.exception.SystemException;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
@Configuration
public class GoogleCloudStorageConfig {

    @Value("${google.cloud.bucket-name}")
    private String homeworkBucketName;
    @Value("${google.cloud.project-id}")
    private String projectId;
    @Value("${google.cloud.credentials-json-path}")
    private String credentialsJsonPath;


    @Bean
    public Bucket getHomeworkBucket(final Storage storage) {
        return storage.get(homeworkBucketName, Storage.BucketGetOption.fields());
    }


    @Bean
    public Storage getStorage() {
        try {
            final Credentials credentials = GoogleCredentials.fromStream(new ClassPathResource(credentialsJsonPath).getInputStream());
            final Storage storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId(projectId)
                    .build()
                    .getService();
            log.info("Connected to google cloud");
            return storage;
        } catch (IOException e) {
            log.error("Error during connection to google cloud");
            throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
