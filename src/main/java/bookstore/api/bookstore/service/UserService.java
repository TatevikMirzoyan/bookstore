package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.FileEntity;
import bookstore.api.bookstore.persistence.repository.UserRepository;
import bookstore.api.bookstore.persistence.entity.BookEntity;
import bookstore.api.bookstore.persistence.entity.UserEntity;
import bookstore.api.bookstore.service.criteria.BookSearchCriteria;
import bookstore.api.bookstore.service.dto.BookDto;
import bookstore.api.bookstore.service.dto.UserDto;
import bookstore.api.bookstore.service.model.csv.User;
import bookstore.api.bookstore.service.model.wrapper.PageResponseWrapper;
import bookstore.api.bookstore.service.criteria.UserSearchCriteria;
import bookstore.api.bookstore.service.model.wrapper.UploadFileResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final int BATCH_SIZE = 20;

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BookService bookService;
    private final PasswordEncoder bcryptEncoder;
    private final FileService fileService;
    private final CsvService<User> csvService;

    public UserDto mapEntityToDto(UserEntity user) {
        return user == null ? null : modelMapper.map(user, UserDto.class);
    }

    public UserEntity mapDtoToEntity(UserDto user) {
        return user == null ? null : modelMapper.map(user, UserEntity.class);
    }

    public void addAll(List<UserEntity> users) {
        userRepository.saveAll(users);
    }

    public UserDto addUser(UserDto user) {
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        UserEntity entity = mapDtoToEntity(user);
        return mapEntityToDto(userRepository.save(entity));
    }

    public UserDto getById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + id + " did not exist"));
        return mapEntityToDto(user);
    }

    public PageResponseWrapper<UserDto> getUsers(UserSearchCriteria criteria) {
        Page<UserEntity> users = userRepository.search(criteria.getFirstName(), criteria.getLastName(),
                criteria.getRole() == null ? null : criteria.getRole().name(), criteria.createPageRequest());
        Page<UserDto> dtos = users.map(this::mapEntityToDto);
        return new PageResponseWrapper<>(dtos.getTotalElements(), dtos.getTotalPages(), dtos.getContent());
    }

    public UserDto updateUser(Long id, UserDto user) {
        user.setId(id);
        return mapEntityToDto(userRepository.save(mapDtoToEntity(user)));
    }

    public void deleteUser(Long id) {
        UserDto dto = getById(id);
        userRepository.deleteById(id);
    }

    public UserDto updateFavoriteBooks(Long id, Long bookId, String function) {
        UserDto user = getById(id);
        BookEntity book = bookService.mapToEntity(bookService.getById(bookId));
        if (function.equalsIgnoreCase("add")) {
            user.addFavoriteBook(book);
        } else if (function.equalsIgnoreCase("remove")) {
            user.removeFavoriteBook(book);
        }
        return updateUser(id, user);
    }

    public PageResponseWrapper<BookDto> getUserFavoriteBooks(Long id, BookSearchCriteria criteria) {
        UserDto user = getById(id);
        Page<BookEntity> books = new PageImpl<>(user.getFavoriteBooks(), criteria.createPageRequest(), user.getFavoriteBooks().size());
        Page<BookDto> dtos = books.map(bookService::mapToDto);
        return new PageResponseWrapper<>(dtos.getTotalElements(), dtos.getTotalPages(), dtos.getContent());
    }

    @Transactional
    public UploadFileResponseWrapper uploadImage(Long id, MultipartFile image) {
        UserDto user = getById(id);

        FileEntity newDoc = new FileEntity();
        newDoc.setExtension(FilenameUtils.getExtension(image.getOriginalFilename()));
        newDoc.setName("user_" + id + "_" + System.currentTimeMillis() + "." + newDoc.getExtension());
        newDoc.setType(image.getContentType());
        newDoc.setSize(image.getSize());
        newDoc.setCreatedAt(LocalDateTime.now());

        fileService.storeFile(image, newDoc.getName());
        FileEntity file = fileService.save(newDoc);
        user.addImage(file);
        updateUser(id, user);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/")
                .path(file.getId().toString())
                .path("/download")
                .toUriString();
        return new UploadFileResponseWrapper(file.getName(), fileDownloadUri, file.getType(), file.getSize());
    }


    public Integer uploadUsersFromCSv(MultipartFile file) throws IOException {
        int count = 0;
        List<List<User>> users = csvService.getEntitiesFromCsv(file, User.class);
        List<List<UserEntity>> entities = users
                .stream()
                .map((list) -> list
                        .stream()
                        .map((temp) -> modelMapper.map(temp, UserEntity.class))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        for (List<UserEntity> list : entities) {
            addAll(list);
            count += list.size();
        }

        for (List<UserEntity> list : entities) {
            List<UserEntity> userEntities = new ArrayList<>();
            for (UserEntity user : list) {
                userEntities.add(user);
                for (int i = 0; i < userEntities.size(); i++) {
                    if (i % BATCH_SIZE == 0 && i > 0) {
                        addAll(userEntities);
                        count += userEntities.size();
                        userEntities.clear();
                    }
                }
                if (userEntities.size() > 0) {
                    addAll(userEntities);
                    count += userEntities.size();
                    userEntities.clear();
                }
            }
        }
        return count;
    }
}
