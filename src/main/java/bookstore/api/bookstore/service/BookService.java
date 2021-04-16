package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.BookEntity;
import bookstore.api.bookstore.persistence.entity.PublisherEntity;
import bookstore.api.bookstore.persistence.repository.BookRepository;
import bookstore.api.bookstore.service.criteria.BookSearchCriteria;
import bookstore.api.bookstore.service.dto.AuthorDto;
import bookstore.api.bookstore.service.dto.BookDto;
import bookstore.api.bookstore.service.dto.PublisherDto;
import bookstore.api.bookstore.service.model.csv.Book;
import bookstore.api.bookstore.service.model.wrapper.PageResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final int BATCH_SIZE = 20;

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final ModelMapper modelMapper;
    private final CsvService<Book> csvService;

    public BookDto mapToDto(BookEntity entity) {
        return modelMapper.map(entity, BookDto.class);
    }

    public BookEntity mapToEntity(BookDto dto) {
        return modelMapper.map(dto, BookEntity.class);
    }

    public BookDto getById(Long id) {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + id + " did not exist"));
        return mapToDto(entity);
    }

    public BookDto getByIsbn(@NotNull String isbn) {
        BookEntity entity = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RecordNotFoundException("Book with ISBN " + isbn + " did not exist"));
        return mapToDto(entity);
    }

    public BookDto addBook(BookDto dto) {
        // TODO: 12-Apr-21 I think here author.getName() method will throw NullPointerException,
        //  if book.getAuthors List is empty
        dto.getAuthors().forEach((author -> {
            try {
                AuthorDto existAuthor = authorService.getById(author.getId());
            } catch (RecordNotFoundException ex) {
                AuthorDto temp = authorService.addAuthor(authorService.mapToDto(author));
                dto.addAuthor(authorService.mapToEntity(temp));
            }
        }));
        PublisherEntity publisher = dto.getPublisher();
        try {
            PublisherDto existPublisher = publisherService.getById(publisher.getId());
        } catch (RecordNotFoundException ex) {
            PublisherDto temp = publisherService.addPublisher(publisherService.mapToDto(publisher));
            dto.setPublisher(publisherService.mapToEntity(temp));
        }
        return mapToDto(bookRepository.save(mapToEntity(dto)));
    }

    public BookDto updateBook(Long id, BookDto dto) {
        BookDto book = getById(id);
        dto.setId(id);
        return addBook(book);
    }

    public PageResponseWrapper<BookDto> getBooks(BookSearchCriteria criteria) {
        Page<BookDto> books = bookRepository.search(criteria.getTitle(), criteria.getIsbn(),
                criteria.getGenre(), criteria.getAuthor(), criteria.getPublisher(), criteria.getPublishedYear(),
                criteria.getPrice(), criteria.getAverageRate(), criteria.createPageRequest()).map(this::mapToDto);
        return new PageResponseWrapper<>(books.getTotalElements(), books.getTotalPages(), books.getContent());
    }

    public void saveAll(List<BookEntity> books) {
        bookRepository.saveAll(books);
    }


    public Integer uploadBooksFromCSv(MultipartFile file) throws IOException {
        // TODO: 15-Apr-21 Add author, publisher part
        int count = 0;
        List<List<Book>> books = csvService.getEntitiesFromCsv(file, Book.class);
        List<List<BookEntity>> entities = books
                .stream()
                .map((list) -> list
                        .stream()
                        .map((temp) -> modelMapper.map(temp, BookEntity.class))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        for (List<BookEntity> list : entities) {
            List<BookEntity> bookList = new ArrayList<>();
            for (BookEntity book : list) {
                bookList.add(book);
                for (int i = 0; i < bookList.size(); i++) {
                    if (i % BATCH_SIZE == 0 && i > 0) {
                        saveAll(bookList);
                        count += bookList.size();
                        bookList.clear();
                    }
                }
                if (bookList.size() > 0) {
                    saveAll(bookList);
                    count += bookList.size();
                    bookList.clear();
                }
            }
        }
        return count;
    }
}
