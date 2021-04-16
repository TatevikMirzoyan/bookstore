package bookstore.api.bookstore.controller;

import bookstore.api.bookstore.service.AuthorService;
import bookstore.api.bookstore.service.criteria.AuthorSearchCriteria;
import bookstore.api.bookstore.service.criteria.SearchCriteria;
import bookstore.api.bookstore.service.dto.AuthorDto;
import bookstore.api.bookstore.service.dto.BookDto;
import bookstore.api.bookstore.service.model.wrapper.PageResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@RestController
@RequestMapping("authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorDto> addAuthor(@Valid @RequestBody AuthorDto dto) {
        AuthorDto temp = authorService.addAuthor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(temp);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getById(id));
    }

    @GetMapping(path = "/{id}/books")
    public ResponseEntity<PageResponseWrapper<BookDto>> getAuthorBooks(@PathVariable Long id,
                                                                       SearchCriteria criteria) {
            return ResponseEntity.ok(authorService.getAuthorBooks(id, criteria));
    }

    @GetMapping
    public ResponseEntity<PageResponseWrapper<AuthorDto>> getAuthors(AuthorSearchCriteria criteria) {
        return ResponseEntity.ok(authorService.getAuthors(criteria));
    }

}
