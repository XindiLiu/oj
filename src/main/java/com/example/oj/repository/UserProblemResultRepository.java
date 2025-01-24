package com.example.oj.repository;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.entity.Problem;
import com.example.oj.entity.UserProblemResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserProblemResultRepository extends CrudRepository<UserProblemResult, UserProblemResult.UserProblemId> {
	Optional<UserProblemResult> findById_UserIdAndId_ProblemId(Long userId, Long problemId);

	Optional<UserProblemResult> findById(UserProblemResult.UserProblemId id);

	Optional<UserProblemResult> findById_UserIdAndStatus(Long userId, SubmissionResultType status);

	Page<UserProblemResult> findAll(Specification<Problem> specification,
									Pageable pageable);


}
