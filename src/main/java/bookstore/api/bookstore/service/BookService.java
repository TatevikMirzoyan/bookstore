package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.*;
import bookstore.api.bookstore.persistence.repository.BookRepository;
import bookstore.api.bookstore.persistence.repository.RateRepository;
import bookstore.api.bookstore.persistence.repository.UserRepository;
import bookstore.api.bookstore.service.criteria.BookSearchCriteria;
import bookstore.api.bookstore.service.dto.*;
import bookstore.api.bookstore.service.model.csv.Book;
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
import java.time.LocalDateTime;
import java.util.*;
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
    private final RateRepository rateRepository;
    private final UserRepository userRepository;
    private final PublisherService publisherService;
    private final AuthorService authorService;
    private final CsvService<Book> csvService;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    public BookService(@Value("${file.upload-dir}") String uploadDir, BookRepository bookRepository, RateRepository rateRepository,
                       UserRepository userRepository, PublisherService publisherService,
                       AuthorService authorService, CsvService<Book> csvService, FileService fileService,
                       ModelMapper modelMapper) {
        this.uploadDir = uploadDir;
        this.bookRepository = bookRepository;
        this.rateRepository = rateRepository;
        this.userRepository = userRepository;
        this.publisherService = publisherService;
        this.authorService = authorService;
        this.csvService = csvService;
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
                author = authorService.getByName(author.getName()).orElse(new AuthorEntity(author.getName()));
                return author;
            })).collect(Collectors.toList()));
        }
        PublisherEntity publisher = dto.getPublisher();
        if (publisher != null) {
            publisher = publisherService.getByName(publisher.getName()).orElse(
                    new PublisherEntity(publisher.getName()));
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
        newDoc.setName("book" + id + "_" + System.currentTimeMillis() + "." + newDoc.getExtension());
        newDoc.setType(image.getContentType());
        newDoc.setSize(image.getSize());
        newDoc.setCreatedAt(LocalDateTime.now());

        fileService.storeFile(new File(image.getOriginalFilename()), newDoc.getName());
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
            try{
             BookDto dto = getByIsbn(book.getIsbn());
              FileEntity file = fileService.uploadBookImageFromPath(book.getImageURL());
                dto.addImage(file);
                updateBook(dto.getId(), dto);
            }catch (RecordNotFoundException e){
                logger.warn(e.getMessage());
            }
        }
    }


    //todo
    public Integer uploadBooksFromCSv(MultipartFile file) throws IOException {
        int count;
        List<Book> books = csvService.getEntitiesFromCsv(file, Book.class);
        List<AuthorEntity> authorList = authorService.findAllAuthors();
        Map<String, AuthorEntity> authorMap = new HashMap<>();
        Map<String, PublisherEntity> publisherMup = new HashMap<>();
        authorList.forEach((authorEntity -> authorMap.put(authorEntity.getName(), authorEntity)));
        List<PublisherEntity> publisherList = publisherService.findAllPublishers();
        publisherList.forEach((publisherEntity -> publisherMup.put(publisherEntity.getName(), publisherEntity)));
        List<BookEntity> entities = books.stream()
                .map((temp) -> {
                    BookEntity book = modelMapper.map(temp, BookEntity.class);
                    book.setAuthors(temp.getAuthors().stream()
                            .map((item) -> {
                                AuthorEntity author;
                                if (authorMap.containsKey(item)) {
                                    author = authorMap.get(item);
                                } else {
                                    author = authorService.addAuthor(new AuthorDto(item));
                                    authorMap.put(item, author);
                                }
                                return author;
                            }).collect(Collectors.toList()));
                    PublisherEntity publisher;
                    if (publisherMup.containsKey(temp.getPublisher())) {
                        publisher = publisherMup.get(temp.getPublisher());
                    } else {
                        publisher = publisherService.addPublisher(new PublisherDto(temp.getPublisher()));
                        publisherMup.put(temp.getPublisher(), publisher);
                    }
                    book.setPublisher(publisher);
                    return book;
                }).collect(Collectors.toList());
        count = batchSave(entities);
        storeImages(entities);
        return count;
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
                logger.warn(e.getMessage());
                continue;
            }
            for (int i = 0; i < bookList.size(); i++) {
                if (i % BATCH_SIZE == 0 && i > 0) {
                    System.out.println("hello from batch");
                    saveAll(bookList);
                    count += bookList.size();
                    bookList.clear();
                }
            }
        }
        if (bookList.size() > 0) {
            System.out.println("hello from batch 2");
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
    public void rateBook(Long userId, Long bookId, Integer number) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User with id " + userId + " did not exist"));
        BookDto book = getById(bookId);
        RateEntity rate = new RateEntity(mapToEntity(book), number, user);
        rate = rateRepository.save(rate);
        book.addRate(rate);
        updateBook(bookId, book);
    }

    public void updateFavoriteBooks(Long userId, Long bookId, String function) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RecordNotFoundException("User with id " + userId + " did not exist"));
        BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " did not exist"));
        if (function.equalsIgnoreCase("add")) {
            user.addFavoriteBook(book);
        } else if (function.equalsIgnoreCase("remove")) {
            user.removeFavoriteBook(book);
        }
        userRepository.save(user);
    }


}
