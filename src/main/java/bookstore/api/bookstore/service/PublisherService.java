package bookstore.api.bookstore.service;

import bookstore.api.bookstore.persistence.entity.PublisherEntity;
import bookstore.api.bookstore.persistence.repository.PublisherRepository;
import bookstore.api.bookstore.service.dto.PublisherDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 * @author Tatevik Mirzoyan
 * Created on 24-Mar-21
 */
@Service
public class PublisherService {
    private final PublisherRepository publisherRepository;
    private final ModelMapper modelMapper;

    public PublisherService(PublisherRepository publisherRepository, ModelMapper modelMapper) {
        this.publisherRepository = publisherRepository;
        this.modelMapper = modelMapper;
    }

    public PublisherDto mapToDto(PublisherEntity entity) {
        return modelMapper.map(entity, PublisherDto.class);
    }

    public PublisherEntity mapToEntity(PublisherDto dto) {
        return modelMapper.map(dto, PublisherEntity.class);
    }

    public PublisherDto getById(Long id) {
        return null;
    }

    public PublisherDto addPublisher(PublisherDto publisher) {
        return null;
    }
}
