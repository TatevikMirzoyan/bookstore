package bookstore.api.bookstore.persistence.repository;

import bookstore.api.bookstore.persistence.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Tatevik Mirzoyan
 * Created on 04-Apr-21
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
