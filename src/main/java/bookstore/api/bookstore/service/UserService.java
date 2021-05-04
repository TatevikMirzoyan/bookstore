package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.FileEntity;
import bookstore.api.bookstore.persistence.entity.RoleEntity;
import bookstore.api.bookstore.persistence.repository.RoleRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ValidationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final int BATCH_SIZE = 30;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
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
        Optional<UserEntity> temp = userRepository.findByUsername(user.getUsername());
        if (temp.isPresent()) {
            throw new ValidationException("User with given username already exists. " + user.getUsername());
        }
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        if (user.getRoles() == null) {
            user.setRoles(Set.of(new RoleEntity("USER")));
        }
        user.setRoles(user.getRoles().stream()
                .map(roleEntity -> {
                    roleEntity = roleRepository.findByRoleName(roleEntity.getRoleName()).orElse(new RoleEntity(roleEntity.getRoleName()));
                    return roleEntity;
                })
                .collect(Collectors.toSet()));
        UserEntity entity = mapDtoToEntity(user);
        return mapEntityToDto(userRepository.save(entity));
    }

    public UserDto getById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User with id " + id + " did not exist"));
        return mapEntityToDto(user);
    }

    public PageResponseWrapper<UserDto> getUsers(UserSearchCriteria criteria) {
        Page<UserEntity> users = userRepository.findAllWithPagination(criteria.getFirstName(), criteria.getLastName(),
                criteria.getUsername(), criteria.getRoles(), criteria.createPageRequest());
        Page<UserDto> dtos = users.map(this::mapEntityToDto);
        return new PageResponseWrapper<>(dtos.getTotalElements(), dtos.getTotalPages(), dtos.getContent());
    }

    public UserDto updateUser(Long id, UserDto dto) {
        getById(id);
        dto.setId(id);
        dto.setPassword(bcryptEncoder.encode(dto.getPassword()));
        UserEntity entity = userRepository.save(mapDtoToEntity(dto));
        return mapEntityToDto(entity);
    }

    public void deleteUser(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }

    public PageResponseWrapper<BookDto> getFavoriteBooks(Long id, BookSearchCriteria criteria) {
        UserDto user = getById(id);
        Page<BookEntity> books = new PageImpl<>(user.getFavoriteBooks(), criteria.createPageRequest(), user.getFavoriteBooks().size());
        Page<BookDto> dtos = books.map((bookEntity -> modelMapper.map(bookEntity, BookDto.class)));
        return new PageResponseWrapper<>(dtos.getTotalElements(), dtos.getTotalPages(), dtos.getContent());
    }

    @Transactional
    public UploadFileResponseWrapper uploadImage(Long id, MultipartFile image) throws IOException {
        UserDto user = getById(id);

        FileEntity newDoc = new FileEntity();
        newDoc.setExtension(FilenameUtils.getExtension(image.getOriginalFilename()));
        newDoc.setName("user_" + id + "_" + System.currentTimeMillis() + "." + newDoc.getExtension());
        newDoc.setType(image.getContentType());
        newDoc.setSize(image.getSize());
        newDoc.setCreatedAt(LocalDateTime.now());

        fileService.storeFile(image, newDoc.getName());
        FileEntity entity = fileService.save(newDoc);
        user.addImage(entity);
        updateUser(id, user);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/")
                .path(entity.getId().toString())
                .path("/download")
                .toUriString();
        return new UploadFileResponseWrapper(entity.getName(), fileDownloadUri, entity.getType(), entity.getSize());
    }

    public Integer uploadUsersFromCSv(MultipartFile file) throws IOException {
        int count = 0;
        Map<String, RoleEntity> roles = new HashMap<>();
        roleRepository.findAll().forEach(roleEntity -> roles.put(roleEntity.getRoleName(), roleEntity));
        List<User> users = csvService.getEntitiesFromCsv(file, User.class);
        List<UserEntity> entities = users.stream()
                .map((temp) -> modelMapper.map(temp, UserEntity.class))
                .collect(Collectors.toList());
        List<String> usernameList = userRepository.findAllUsernames();
        List<UserEntity> userEntities = new ArrayList<>();
        for (UserEntity user : entities) {
            try {
                isValidUsername(user, usernameList);
            } catch (ValidationException e) {
                logger.warn(e.getMessage());
                continue;
            }
            user.setPassword(bcryptEncoder.encode(user.getPassword()));
            if (user.getRoles() == null) {
                user.setRoles(Set.of(new RoleEntity("USER")));
            }
            user.setRoles(user.getRoles().
                    stream().map(roleEntity -> {
                if (roles.containsKey(roleEntity.getRoleName())) {
                    roleEntity = roles.get(roleEntity.getRoleName());
                } else {
                    roleEntity = new RoleEntity(roleEntity.getRoleName());
                }
                return roleEntity;
            }).collect(Collectors.toSet()));
            userEntities.add(user);
            usernameList.add(user.getUsername());
            for (int i = 0; i < userEntities.size(); i++) {
                if (i % BATCH_SIZE == 0 && i > 0) {
                    addAll(userEntities);
                    count += userEntities.size();
                    userEntities.clear();
                }
            }
        }
        if (userEntities.size() > 0) {
            addAll(userEntities);
            count += userEntities.size();
            userEntities.clear();
        }

        return count;
    }

    private void isValidUsername(UserEntity user, List<String> usernameList) {
        if (usernameList.contains(user.getUsername())) {
            throw new ValidationException("User with given username already exists. " + user.getUsername());
        }
    }

}