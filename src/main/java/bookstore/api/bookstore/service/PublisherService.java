package bookstore.api.bookstore.service;

import bookstore.api.bookstore.exceptions.RecordNotFoundException;
import bookstore.api.bookstore.persistence.entity.PublisherEntity;
import bookstore.api.bookstore.persistence.repository.PublisherRepository;
import bookstore.api.bookstore.service.dto.PublisherDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Tatevik Mirzoyan
 * Created on 24-Mar-21
 */
@Service
@RequiredArgsConstructor
public class PublisherService {
    private final PublisherRepository publisherRepository;
    private final ModelMapper modelMapper;

    public PublisherDto mapToDto(PublisherEntity entity) {
        return modelMapper.map(entity, PublisherDto.class);
    }

    public PublisherEntity mapToEntity(PublisherDto dto) {
        return modelMapper.map(dto, PublisherEntity.class);
    }

    public PublisherDto getById(Long id) {
        PublisherEntity publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Publisher with id " + id + " did not exist"));;
        return mapToDto(publisher);
    }

    public  Optional<PublisherEntity> getByName(String name) {
        return publisherRepository.findByName(name);
    }

    public PublisherEntity addPublisher(PublisherDto publisher) {
        if (publisher != null){
            PublisherEntity entity = mapToEntity(publisher);
            entity = publisherRepository.save(entity);
            return entity;
        } else throw new NullPointerException("Publisher must not be null");
    }

    List<PublisherEntity> findAllPublishers(){
        return publisherRepository.findAllPublishers();
    }
}
