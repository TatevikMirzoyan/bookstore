package bookstore.api.bookstore.controller;

import bookstore.api.bookstore.service.BookService;
import bookstore.api.bookstore.service.criteria.BookSearchCriteria;
import bookstore.api.bookstore.service.dto.BookDto;
import bookstore.api.bookstore.service.model.wrapper.PageResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@RestController
@RequestMapping("books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookDto> addBook(@Valid @RequestBody BookDto dto) {
        BookDto book = bookService.addBook(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<BookDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto dto) {
        BookDto book = bookService.updateBook(id, dto);
        return ResponseEntity.ok(book);
    }

    @GetMapping
    public ResponseEntity<PageResponseWrapper<BookDto>> getBooks(BookSearchCriteria criteria) {
        return ResponseEntity.ok(bookService.getBooks(criteria));
    }

    @PostMapping("/upload-csv-file")
    public ResponseEntity<?> uploadUsersFromCSV(@NotEmpty(message = "The given file must not be null or empty")
                                                @RequestParam("file") MultipartFile file) throws IOException {

        int count = bookService.uploadBooksFromCSv(file);
        return ResponseEntity.ok().body(Map.of("message", "File is uploaded successfully. Saved " + count + " books."));
    }
}
