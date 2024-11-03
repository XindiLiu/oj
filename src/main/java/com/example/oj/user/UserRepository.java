package com.example.oj.user;

import com.example.oj.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;

public interface UserRepository extends CrudRepository<User, Long>, PagingAndSortingRepository<User, Long> {
	User save(User user);

	User findByUsername(String username);

	User getUserById(Long id);

	boolean existsByUsername(String username);

	LocalDateTime getCreateTimeById(Long id);

}