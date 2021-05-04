package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.FileStorageException;
import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.FileEntity;
import bookstore.api.bookstore.persistence.repository.FileRepository;
import bookstore.api.bookstore.service.dto.BookDto;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

/**
 * @author Tatevik Mirzoyan
 * Created on 04-Apr-21
 */
@Service
public class FileService {

    private final String uploadDir;
    private final Path fileStorageLocation;
    private final FileRepository fileRepository;
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    public FileService(@Value("${file.upload-dir}") String uploadDir, FileRepository fileRepository) {
        this.uploadDir = uploadDir;
        this.fileRepository = fileRepository;
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileEntity save(FileEntity entity) {
        return fileRepository.save(entity);
    }

    public void storeFile(MultipartFile file, String fileName) {
        try {
            String uploadPath = Paths.get(String.join(File.separator, uploadDir, fileName)).normalize().toString();
            File newFile = new File(uploadPath);
            file.transferTo(newFile);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + file.getName() + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) throws FileNotFoundException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists())
                throw new MalformedURLException();

            return resource;
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    public FileEntity getById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("File with id " + id + " did not exist"));
    }


    @Transactional
    public FileEntity uploadImageFromURL(String path) throws IOException, URISyntaxException {
        URL url = new URL(path);
        String extension = FilenameUtils.getExtension(path);
        String fileName = FilenameUtils.getBaseName(path) + "_" + System.currentTimeMillis();
        String targetLocation = this.fileStorageLocation.normalize().toString() + "\\" + fileName;
        File newFile = new File(targetLocation);
        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(newFile.getPath())) {
            byte[] buffer = new byte[2048];
            int bytesCount;
            while ((bytesCount = is.read(buffer)) > 0) {
                os.write(buffer, 0, bytesCount);
            }
        } catch (MalformedURLException e) {
            logger.warn("MalformedURLException " + e.getMessage());
        } catch (FileNotFoundException e) {
            logger.warn("FileNotFoundException " + e.getMessage());
        } catch (IOException e) {
            logger.warn("IOException " + e.getMessage());
        }
        FileEntity newDoc = new FileEntity();
        newDoc.setExtension(extension);
        newDoc.setName(fileName);

        newDoc.setType(Files.probeContentType(newFile.toPath()));
        newDoc.setSize(newFile.length());
        newDoc.setCreatedAt(LocalDateTime.now());
        return save(newDoc);
    }
}
