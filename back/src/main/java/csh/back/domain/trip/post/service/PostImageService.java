package csh.back.domain.trip.post.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class PostImageService {

    private static final String IMAGE_DIR = "uploadedimages";

    public String saveImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("업로드된 이미지가 없습니다.");
        }

        try {

            Path uploadDir = Paths.get(IMAGE_DIR);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalName = image.getOriginalFilename();

            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension =
                        originalName.substring(originalName.lastIndexOf("."));
            }

            String savedName =
                    UUID.randomUUID() + extension;

            Path savePath =
                    uploadDir.resolve(savedName);

            image.transferTo(savePath);

            return "/uploadedimages/" + savedName;

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}