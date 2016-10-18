package ftp.core.repository.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface GenericRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

}
