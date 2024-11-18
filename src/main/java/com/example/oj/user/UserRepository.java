package com.example.oj.user;

import com.example.oj.submission.SubmissionSimple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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

//	Page<SubmissionSimple> findSimpleByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
}
