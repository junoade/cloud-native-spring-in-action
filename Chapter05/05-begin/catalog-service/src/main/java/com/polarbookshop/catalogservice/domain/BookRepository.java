package com.polarbookshop.catalogservice.domain;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {

	Optional<Book> findByIsbn(String isbn);
	boolean existsByIsbn(String isbn);

	@Modifying
	@Transactional
	@Query("DELETE FROM Book b where isbn = :isbn")
	void deleteByIsbn(@Param("isbn") String isbn);

}
