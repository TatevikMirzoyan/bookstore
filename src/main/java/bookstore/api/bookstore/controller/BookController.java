package bookstore.api.bookstore.controller;

import bookstore.api.bookstore.configuration.security.session.SessionUser;
import bookstore.api.bookstore.service.BookService;
import bookstore.api.bookstore.service.criteria.BookSearchCriteria;
import bookstore.api.bookstore.service.dto.BookDto;
import bookstore.api.bookstore.service.model.wrapper.PageResponseWrapper;
import bookstore.api.bookstore.service.model.wrapper.UploadFileResponseWrapper;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("books")
@SessionAttributes(SESSION_USER_KEY)
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDto> addBook(@Valid @RequestBody BookDto dto) {
        BookDto book = bookService.addBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BookDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PutMapping(path = "/{id}")
   // @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto dto) {
        BookDto book = bookService.updateBook(id, dto);
        return ResponseEntity.ok(book);
    }

    @GetMapping
    public ResponseEntity<PageResponseWrapper<BookDto>> getBooks(BookSearchCriteria criteria) {
        return ResponseEntity.ok(bookService.getBooks(criteria));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<UploadFileResponseWrapper> uploadImage(@PathVariable Long id,
                                                                 @NotNull(message = "The given image must not be null")
                                                                 @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(bookService.uploadImage(id, image));
    }

    @PostMapping("/upload-csv-file")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadUsersFromCSV(@NotEmpty(message = "The given file must not be null or empty")
                                                @RequestParam("file") MultipartFile file) throws IOException {

        Integer count = bookService.uploadBooksFromCSv(file);
        return ResponseEntity.ok().body(Map.of("message", "File is uploaded successfully. Saved " + count + " books."));
    }

    @PutMapping(path = "/{id}/rate")
    public ResponseEntity<?> rateBook(@ModelAttribute(SESSION_USER_KEY) SessionUser sessionUser, @PathVariable Long id, @RequestParam("rate") Integer rate) {
        bookService.rateBook(sessionUser.getId(), id, rate);
        return ResponseEntity.ok().body(Map.of("message", "Book is rated successfully."));

    }

    @PutMapping(path = "/{id}/favorite")
    public ResponseEntity<?> updateFavoriteBooks(@ModelAttribute(SESSION_USER_KEY) SessionUser sessionUser,@PathVariable Long id,
                                                       @RequestParam(name = "function", defaultValue = "add") String function) {
        bookService.updateFavoriteBooks(sessionUser.getId(),id, function);
        return ResponseEntity.ok().body(Map.of("message", "Favorite books are updated successfully."));
    }
}
