package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.FileStorageException;
import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.AuthorEntity;
import bookstore.api.bookstore.persistence.entity.FileEntity;
import bookstore.api.bookstore.persistence.repository.FileRepository;
import bookstore.api.bookstore.service.dto.AuthorDto;
import bookstore.api.bookstore.service.dto.FileDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * @author Tatevik Mirzoyan
 * Created on 04-Apr-21
 */
@Service
public class FileService {

    private final String uploadDir;
    private final Path fileStorageLocation;
    private final FileRepository fileRepository;
    private final ModelMapper modelMapper;

    public FileService(@Value("${file.upload-dir}") String uploadDir, FileRepository fileRepository, ModelMapper modelMapper) {
        this.uploadDir = uploadDir;
        this.fileRepository = fileRepository;
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();
        this.modelMapper = modelMapper;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileDto mapToDto(FileEntity entity) {
        return modelMapper.map(entity, FileDto.class);
    }

    public FileEntity mapToEntity(FileDto dto) {
        return modelMapper.map(dto, FileEntity.class);
    }

    public FileEntity save(FileEntity entity) {
        return fileRepository.save(entity);
    }

    public void storeFile(MultipartFile file, String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
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

    public FileDto getById(Long id) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("File with id " + id + " did not exist"));
        ;
        return mapToDto(file);
    }


//    public Long uploadFileFromPath(String path, String docType) throws IOException {
//        File temp = new File("image.jpg");
//        URL url = new URL(path);
//        try (InputStream is = url.openStream();
//             OutputStream os = new FileOutputStream(temp)) {
//            byte[] buffer = new byte[2048];
//            int bytesCount;
//            while ((bytesCount = is.read(buffer)) > 0) {
//                os.write(buffer, 0, bytesCount);
//            }
//        }
//        MultipartFile file = new MockMultipartFile(temp.getName(), temp.getName(),
//                "images/jpeg", Files.readAllBytes(temp.toPath()));
//
//        return getDocumentIdByName(uploadFile(file, docType));
//    }

}
