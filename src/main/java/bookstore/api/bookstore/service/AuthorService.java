package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.AuthorEntity;
import bookstore.api.bookstore.persistence.repository.AuthorRepository;
import bookstore.api.bookstore.service.criteria.AuthorSearchCriteria;
import bookstore.api.bookstore.service.criteria.SearchCriteria;
import bookstore.api.bookstore.service.dto.AuthorDto;
import bookstore.api.bookstore.service.dto.BookDto;
import bookstore.api.bookstore.service.model.wrapper.PageResponseWrapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author Tatevik Mirzoyan
 * Created on 22-Mar-21
 */
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;

    public AuthorService(AuthorRepository authorRepository, ModelMapper modelMapper) {
        this.authorRepository = authorRepository;
        this.modelMapper = modelMapper;
    }

    public AuthorDto mapToDto(AuthorEntity entity) {
        return modelMapper.map(entity, AuthorDto.class);
    }

    public AuthorEntity mapToEntity(AuthorDto dto) {
        return modelMapper.map(dto, AuthorEntity.class);
    }

    public AuthorDto addAuthor(AuthorDto dto) {
        AuthorEntity entity = mapToEntity(dto);
        entity = authorRepository.save(entity);
        return mapToDto(entity);
    }

    public AuthorDto getById(Long id) {
        AuthorEntity entity = authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + id + " did not exist"));
        return mapToDto(entity);
    }

    public PageResponseWrapper<BookDto> getAuthorBooks(long id, SearchCriteria criteria) {
        AuthorDto author = getById(id);
        Page<BookDto> books = new PageImpl<>(author.getBooks()
                .stream()
                .map((item) -> modelMapper.map(item,BookDto.class))
                .collect(Collectors.toList()),criteria.createPageRequest(),author.getBooks().size());
        return new PageResponseWrapper<>(books.getTotalElements(), books.getTotalPages(), books.getContent());
    }

    public PageResponseWrapper<AuthorDto> getAuthors(AuthorSearchCriteria criteria) {
        Page<AuthorDto> authors = authorRepository.findByNameContaining(criteria.getName(), criteria.createPageRequest()).map(this::mapToDto);
        return new PageResponseWrapper<>(authors.getTotalElements(), authors.getTotalPages(), authors.getContent());
    }

}
