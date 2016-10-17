package ftp.core.persistance.face.generic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface GenericRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

}
