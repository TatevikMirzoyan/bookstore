package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.*;
import bookstore.api.bookstore.persistence.repository.BookRepository;
import bookstore.api.bookstore.persistence.repository.RateRepository;
import bookstore.api.bookstore.persistence.repository.UserRepository;
import bookstore.api.bookstore.service.criteria.BookSearchCriteria;
import bookstore.api.bookstore.service.dto.*;
import bookstore.api.bookstore.service.model.csv.Book;
import bookstore.api.bookstore.service.model.csv.Rate;
import bookstore.api.bookstore.service.model.wrapper.PageResponseWrapper;
import bookstore.api.bookstore.service.model.wrapper.UploadFileResponseWrapper;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Service
public class BookService {

    private final int BATCH_SIZE = 20;
    private final String uploadDir;
    private final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final RateRepository rateRepository;
    private final PublisherService publisherService;
    private final AuthorService authorService;
    private final CsvService<Book> csvService;
    private final CsvService<Rate> rateCsvService;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    public BookService(@Value("${file.upload-dir}") String uploadDir, BookRepository bookRepository,
                       UserRepository userRepository, RateRepository rateRepository, PublisherService publisherService,
                       AuthorService authorService, CsvService<Book> csvService, CsvService<Rate> rateCsvService, FileService fileService,
                       ModelMapper modelMapper) {
        this.uploadDir = uploadDir;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.rateRepository = rateRepository;
        this.publisherService = publisherService;
        this.authorService = authorService;
        this.csvService = csvService;
        this.rateCsvService = rateCsvService;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
    }

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

    @Transactional
    public BookDto addBook(BookDto dto) {
        if (dto.getAuthors() != null) {
            dto.setAuthors(dto.getAuthors().stream().map((author -> {
                author = authorService.getByName(author.getName().toUpperCase()).orElse(new AuthorEntity(author.getName().toUpperCase()));
                return author;
            })).collect(Collectors.toList()));
        }
        PublisherEntity publisher = dto.getPublisher();
        if (publisher != null) {
            publisher = publisherService.getByName(publisher.getName().toUpperCase()).orElse(
                    new PublisherEntity(publisher.getName().toUpperCase()));
            dto.setPublisher(publisher);
        }
        if (dto.getPrice() == null) {
            dto.setPrice(0.0);
        }
        if (dto.getAverageRate() == null) {
            dto.setAverageRate(0.0);
        }
        return mapToDto(bookRepository.save(mapToEntity(dto)));
    }

    public BookDto updateBook(Long id, BookDto dto) {
        getById(id);
        dto.setId(id);
        return addBook(dto);
    }

    public PageResponseWrapper<BookDto> getBooks(BookSearchCriteria criteria) {
        Page<BookDto> books = bookRepository.findAllWithPagination(criteria.getTitle(), criteria.getIsbn(),
                criteria.getGenre(), criteria.getAuthor(), criteria.getPublisher(), criteria.getPublishedYear(),
                criteria.getMinPrice(), criteria.getMinRate(), criteria.createPageRequest()).map(this::mapToDto);
        return new PageResponseWrapper<>(books.getTotalElements(), books.getTotalPages(), books.getContent());
    }

    public void saveAll(List<BookEntity> books) {
        bookRepository.saveAll(books);
    }

    @Transactional
    public UploadFileResponseWrapper uploadImage(Long id, MultipartFile image) {
        BookDto book = getById(id);
        FileEntity newDoc = new FileEntity();
        newDoc.setExtension(FilenameUtils.getExtension(image.getOriginalFilename()));
        newDoc.setName("book_" + id + "_" + System.currentTimeMillis() + "." + newDoc.getExtension());
        newDoc.setType(image.getContentType());
        newDoc.setSize(image.getSize());
        newDoc.setCreatedAt(LocalDateTime.now());

        fileService.storeFile(image, newDoc.getName());
        FileEntity file = fileService.save(newDoc);
        book.addImage(file);
        updateBook(id, book);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/")
                .path(file.getId().toString())
                .path("/download")
                .toUriString();
        return new UploadFileResponseWrapper(file.getName(), fileDownloadUri, file.getType(), file.getSize());
    }

    public void storeImages(List<BookEntity> entities) throws IOException {
        for (BookEntity book : entities) {
            try {
                BookDto dto = getByIsbn(book.getIsbn());
                if (dto.getImages() == null || dto.getImages().isEmpty()) {
                    FileEntity file = fileService.uploadImageFromURL(book.getImageURL());
                    dto.addImage(file);
                    updateBook(dto.getId(), dto);
                }
            } catch (RecordNotFoundException | URISyntaxException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    public Integer uploadBooksFromCsv(MultipartFile file) throws IOException {
        int count;
        List<Book> books = csvService.getEntitiesFromCsv(file, Book.class);
        List<AuthorEntity> authorList = authorService.findAllAuthors();
        Map<String, AuthorEntity> authorMap = new HashMap<>();
        Map<String, PublisherEntity> publisherMup = new HashMap<>();
        authorList.forEach((authorEntity -> authorMap.put(authorEntity.getName().toUpperCase(), authorEntity)));
        List<PublisherEntity> publisherList = publisherService.findAllPublishers();
        publisherList.forEach((publisherEntity -> publisherMup.put(publisherEntity.getName().toUpperCase(), publisherEntity)));
        List<BookEntity> entities = books.stream()
                .map((temp) -> {
                    BookEntity book = modelMapper.map(temp, BookEntity.class);
                    book.setAuthors(getAuthorList(temp.getAuthors(), authorMap));
                    book.setPublisher(getPublisher(temp.getPublisher(), publisherMup));
                    return book;
                }).collect(Collectors.toList());
        count = batchSave(entities);
        storeImages(entities);
        return count;
    }

    private List<AuthorEntity> getAuthorList(List<String> authors, Map<String, AuthorEntity> authorMap) {
        return authors.stream()
                .map((authorName) -> {
                    AuthorEntity author;
                    if (authorMap.containsKey(authorName.toUpperCase())) {
                        author = authorMap.get(authorName.toUpperCase());
                    } else {
                        author = authorService.addAuthor(new AuthorDto(authorName.toUpperCase()));
                        authorMap.put(authorName.toUpperCase(), author);
                    }
                    return author;
                }).collect(Collectors.toList());
    }

    private PublisherEntity getPublisher(String publisher, Map<String, PublisherEntity> publisherMup) {
        PublisherEntity entity;
        if (publisherMup.containsKey(publisher.toUpperCase())) {
            entity = publisherMup.get(publisher.toUpperCase());
        } else {
            entity = publisherService.addPublisher(new PublisherDto(publisher.toUpperCase()));
            publisherMup.put(publisher.toUpperCase(), entity);
        }
        return entity;
    }

    private Integer batchSave(List<BookEntity> list) {
        int count = 0;
        List<BookEntity> bookList = new ArrayList<>();
        List<String> bookTitleList = bookRepository.findAllBookTitles();
        List<String> bookIsbnList = bookRepository.findAllBookIsbn();
        for (BookEntity book : list) {
            try {
                isValid(book, bookTitleList, bookIsbnList);
                bookList.add(book);
            } catch (ValidationException e) {
                //   logger.warn(e.getMessage());
                continue;
            }
            for (int i = 0; i < bookList.size(); i++) {
                if (i % BATCH_SIZE == 0 && i > 0) {
                    saveAll(bookList);
                    count += bookList.size();
                    bookList.clear();
                }
            }
        }
        if (bookList.size() > 0) {
            saveAll(bookList);
            count += bookList.size();
            bookList.clear();
        }
        return count;
    }

    private void isValid(BookEntity book, List<String> bookTitleList, List<String> bookIsbnList) {
        if (bookIsbnList.contains((book.getIsbn()))) {
            throw new ValidationException("Book with given ISBN already exists. " + book.getIsbn());
        }
        if (bookTitleList.contains(book.getTitle())) {
            throw new ValidationException("Book with given title already exists. " + book.getTitle());
        }
        if (book.getAverageRate() == null) {
            book.setAverageRate(0.0);
        }
        if (book.getPrice() == null) {
            book.setPrice(0.0);
        }

    }

    @Transactional
    public void rateBook(Long id, Long bookId, Integer number) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("User with id " + id + " did not exist"));
        BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " did not exist"));
        RateEntity rate = new RateEntity(book, number, user);
        book.getRates().add(rate);
        book.setRates(book.getRates());
        book.setAverageRate(book.composeAverageRate(book.getRates()));
        bookRepository.save(book);
    }

    @Transactional
    public String updateFavoriteBooks(Long userId, Long bookId, String function) {
        String result = null;
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User with id " + userId + " did not exist"));
        BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " did not exist"));
        if (function.equalsIgnoreCase("add")) {
            if (!user.getFavoriteBooks().contains(book)) {
                user.getFavoriteBooks().add(book);
                user.setFavoriteBooks(user.getFavoriteBooks());
                userRepository.save(user);
                result = "The Book is added into User favorite books list successfully.";
            } else result = "The Book is already in User favorite books list.";
        } else if (function.equalsIgnoreCase("remove")) {
            if (user.getFavoriteBooks() != null && user.getFavoriteBooks().contains(book)) {
                user.getFavoriteBooks().remove(book);
                user.setFavoriteBooks(user.getFavoriteBooks());
                userRepository.save(user);
                result = "The Book is removed from User favorite books list successfully.";
            } else result = "The user favorite books is empty, or did not contain this book";
        }
        return result;
    }

    public Integer uploadBooksRatesFromCsv(MultipartFile file) throws IOException {
        AtomicInteger count = new AtomicInteger();
        List<Rate> rates = rateCsvService.getEntitiesFromCsv(file, Rate.class);
        List<RateEntity> entities = rates.stream()
                .map(rate -> {
                    try {
                        UserEntity user = userRepository.findById(rate.getUserId())
                                .orElseThrow(() -> new RecordNotFoundException("User with given Id " + rate.getUserId() + " did not exist."));
                        BookEntity book = bookRepository.findByIsbn(rate.getIsbn())
                                .orElseThrow(() -> new RecordNotFoundException("Book with given ISBN " + rate.getIsbn() + " did not exist."));
                        return new RateEntity(book, rate.getRate(), user);
                    } catch (RecordNotFoundException e) {
                        logger.warn(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        entities.forEach(rateEntity -> {
            BookEntity book = rateEntity.getBook();
            book.getRates().add(rateEntity);
            book.setRates(book.getRates());
            book.setAverageRate(book.composeAverageRate(book.getRates()));
            bookRepository.save(book);
            count.getAndIncrement();
        });
        return count.get();
    }

    public Object getRates(Long id) {
        List<RateEntity> rates = rateRepository.findAllByBookId(id);
        return (rates == null) || (rates.size() == 0) ? "The Book is not rated yet." : rates.stream().mapToInt((RateEntity::getRate));
    }
}
