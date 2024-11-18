package com.example.oj.userProblem;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.problem.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserProblemRepository extends CrudRepository<UserProblem, UserProblem.UserProblemId> {
	Optional<UserProblem> findById_UserIdAndId_ProblemId(Long userId, Long problemId);

	Optional<UserProblem> findById(UserProblem.UserProblemId id);

	Optional<UserProblem> findById_UserIdAndStatus(Long userId, SubmissionResultType status);

	Page<UserProblem> findAll(Specification<Problem> specification,
							  Pageable pageable);


}
