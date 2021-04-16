package bookstore.api.bookstore.controller;

import bookstore.api.bookstore.configuration.security.session.SessionUser;
import bookstore.api.bookstore.service.UserService;
import bookstore.api.bookstore.service.criteria.BookSearchCriteria;
import bookstore.api.bookstore.service.criteria.UserSearchCriteria;
import bookstore.api.bookstore.service.dto.BookDto;
import bookstore.api.bookstore.service.dto.UserDto;
import bookstore.api.bookstore.service.model.wrapper.PageResponseWrapper;
import bookstore.api.bookstore.service.model.wrapper.UploadFileResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.util.Map;

import static bookstore.api.bookstore.configuration.security.session.SessionUser.SESSION_USER_KEY;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@RestController
@RequestMapping("users")
@SessionAttributes(SESSION_USER_KEY)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto user) {
        UserDto dto = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping(path = "/{id}/favorite-books")
    public ResponseEntity<PageResponseWrapper<BookDto>> getUserFavoriteBooks(@PathVariable Long
                                                                                     id, BookSearchCriteria criteria) {
        PageResponseWrapper<BookDto> result = userService.getUserFavoriteBooks(id, criteria);
        return ResponseEntity.ok(result);
    }

    @PutMapping(path = "/{id}/favorite-books")
    public ResponseEntity<UserDto> updateFavoriteBooks(@PathVariable Long id,
                                                       @RequestParam(value = "bookId") Long bookId,
                                                       @RequestParam(name = "function", defaultValue = "add") String function) {
        UserDto user = userService.updateFavoriteBooks(id, bookId, function);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<PageResponseWrapper<UserDto>> getUsers(UserSearchCriteria criteria) {
        return ResponseEntity.ok(userService.getUsers(criteria));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto user) {
        UserDto dto = userService.updateUser(id, user);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/{id}/images")
    public ResponseEntity<UploadFileResponseWrapper> uploadImage(@PathVariable Long id,
                                                                 @NotNull(message = "The given image must not be null")
                                                                 @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(userService.uploadImage(id, image));
    }


    @PostMapping("/upload-csv-file")
    public ResponseEntity<?> uploadUsersFromCSV(@NotEmpty(message = "The given file must not be null or empty")
                                                @RequestParam("file") MultipartFile file) throws IOException {

        int count = userService.uploadUsersFromCSv(file);
        return ResponseEntity.ok().body(Map.of("message", "File is uploaded successfully. Saved " + count + " users."));
    }

    // This is just an example of how to get user session user data
    // You can use '@ModelAttribute(SESSION_USER_KEY) SessionUser sessionUser' part in any api call where its needed
    @GetMapping("/session")
    public ResponseEntity<SessionUser> getSessionUser(@ModelAttribute(SESSION_USER_KEY) SessionUser sessionUser) {
        return ResponseEntity.ok(sessionUser);
    }
}
