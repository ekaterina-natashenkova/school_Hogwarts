package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public AvatarService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar");

        Student student = studentRepository.getById(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
            logger.debug("The file will be saved at path: {}, extension: {}", filePath, getExtension(file.getOriginalFilename()));

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
            logger.debug("File saved successfully. Path: {}, size: {} bytes", filePath, file.getSize());
        }
        /**
         * альтернативный вариант для строк 42-48 (вместо блока try-with-resources)
         * Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
         */

        Avatar avatar = findAvatar(studentId);
        if (avatar == null) {
            logger.debug("Creating a new avatar entry for a student with ID: {}", studentId);
            avatar = new Avatar();
        }
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
        logger.debug("Updating an existing avatar entry for a student with ID: {}", studentId);
    }

    public Avatar findAvatar(long studentId) {
        logger.info("Was invoked method for search avatar by student id");
        logger.warn("Avatar by student with ID {} not found", studentId);
        return avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
    }

    private String getExtension(String fileName) {
        logger.info("Was invoked method for get extension");
        logger.debug("Get extension file: {}", fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public Collection<Avatar> getAll() {
        logger.info("Was invoked method for get all avatars");
        logger.debug("Request all avatars");
        return avatarRepository.findAll();
    }

    public Collection<Avatar> getAllAvatarsByPage(Integer pageNumber, Integer pageSize) {
        logger.info("Was invoked method for get all avatars by page");
        PageRequest pageRequest = PageRequest.of(pageNumber,pageSize);
        logger.debug("Request for avatars. Page: {}, page size: {}", pageNumber, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }

}
