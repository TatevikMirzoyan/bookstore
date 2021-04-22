package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.FileStorageException;
import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.FileEntity;
import bookstore.api.bookstore.persistence.repository.FileRepository;
import bookstore.api.bookstore.service.dto.BookDto;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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

    public void storeFile(File file, String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
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
    public FileEntity uploadBookImageFromPath(String path) throws IOException {
        File file = new File(path);
        URL url = file.toURI().toURL();
        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[2048];
            int bytesCount;
            while ((bytesCount = is.read(buffer)) > 0) {
                os.write(buffer, 0, bytesCount);
            }
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException :- " + e.getMessage());

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException :- " + e.getMessage());

        } catch (IOException e) {
            System.out.println("IOException :- " + e.getMessage());
        }

        FileEntity newDoc = new FileEntity();
        newDoc.setExtension(FilenameUtils.getExtension(file.getName()));
        newDoc.setName("book " + "_image_" + System.currentTimeMillis() + "." + newDoc.getExtension());
        URLConnection connection = file.toURI().toURL().openConnection();
        newDoc.setType(connection.getContentType());
        // TODO: 22-Apr-21 here throws "exception": "java.nio.file.InvalidPathException",
        //    "message": "Illegal char <:> at index 4: http:\\images.amazon.com
        newDoc.setSize(Files.size(file.toPath()));
        newDoc.setCreatedAt(LocalDateTime.now());

        storeFile(file, newDoc.getName());
        return save(newDoc);
    }
}
