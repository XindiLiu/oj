package com.example.oj.repository;

import com.example.oj.entity.User;
import com.example.oj.dto.UserSimpleProj;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Long>, PagingAndSortingRepository<User, Long> {
	User save(User user);

	User findByUsername(String username);

	User getUserById(Long id);

	UserSimpleProj findUserSimpleById(Long id);

	boolean existsByUsername(String username);

	LocalDateTime getCreateTimeById(Long id);

	@Modifying
	@Query("update User u set u.score = :score where u.id = :id")
	void updateScore(Long id, Long score);

	@Query("select u.score from User u where u.id = :id")
	Long getScoreById(Long id);

	@Modifying
	@Query("UPDATE User u SET u.score = u.score + :scoreDiff WHERE u.id = :id")
	void incrementScore(Long id, Long scoreDiff);

	@Query("select u.password from User u where u.id = :id")
	String getPasswordById(Long id);

	@Modifying
	@Query("update User u set u.password = :password where u.id = :id")
	int updatePasswordById(Long id, String password);

	@Query("SELECT u FROM User u ORDER BY u.score DESC")
	List<User> findAllUsersOrderByScore();

}
