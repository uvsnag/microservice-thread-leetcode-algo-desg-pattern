package com.example.fileservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class StorageService {

    private final S3Client s3Client;
    private final String bucket;

    public StorageService(S3Client s3Client, @Value("${app.storage.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public List<String> listFiles(String folder) {
        String prefix = buildPrefix(folder);
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();
        return s3Client.listObjectsV2(request)
                .contents()
                .stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    public byte[] downloadFile(String folder, String filename) {
        String key = buildPrefix(folder) + filename;
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(request);
        return objectBytes.asByteArray();
    }

    private String buildPrefix(String folder) {
        return StringUtils.hasText(folder) ? folder + "/" : "";
    }
}
