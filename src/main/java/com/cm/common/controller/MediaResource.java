package com.cm.common.controller;

import com.cm.common.model.dto.MediaDTO;
import com.cm.common.model.enumeration.MediaType;
import com.cm.common.service.media.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaResource {

    private final MediaService mediaService;


    @PostMapping("/upload/{lessonId}")
    public ResponseEntity<MediaDTO> uploadFile(@RequestParam("file") final MultipartFile file,
                                               @PathVariable("lessonId") final Long lessonId,
                                               @RequestParam("type") final MediaType type) {
        return ResponseEntity.ok().body(mediaService.upload(lessonId, type, file));
    }


    @PostMapping("/update/{mediaId}")
    public ResponseEntity<MediaDTO> updateMedia(@RequestParam("file") final MultipartFile file,
                                                @PathVariable("mediaId") final Long mediaId) {
        return ResponseEntity.ok().body(mediaService.update(file, mediaId));
    }

    @PostMapping("/delete")
    public void deleteMedia(@RequestParam("mediaId") final Long mediaId) {
        mediaService.deleteMediaById(mediaId);
    }


}
