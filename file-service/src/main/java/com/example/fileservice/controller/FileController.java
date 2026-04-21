package com.example.fileservice.controller;

import java.util.List;

import com.example.fileservice.event.FileActivityPublisher;
import com.example.fileservice.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Files", description = "AWS S3 file operations")
@RestController
@RequestMapping("/files")
public class FileController {

    private final StorageService storageService;
    private final FileActivityPublisher fileActivityPublisher;

    public FileController(StorageService storageService, FileActivityPublisher fileActivityPublisher) {
        this.storageService = storageService;
        this.fileActivityPublisher = fileActivityPublisher;
    }

    @Operation(summary = "List files in S3 bucket (optionally under a folder)")
    @GetMapping
    public List<String> listFiles(@RequestParam(required = false) String folder) {
        List<String> files = storageService.listFiles(folder);
        fileActivityPublisher.publish("LISTED", folder, null, files.size());
        return files;
    }

    @Operation(summary = "Download a file from S3")
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam(required = false) String folder,
            @RequestParam String filename
    ) {
        byte[] fileBytes = storageService.downloadFile(folder, filename);
        fileActivityPublisher.publish("DOWNLOADED", folder, filename, 1);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(fileBytes);
    }
}
