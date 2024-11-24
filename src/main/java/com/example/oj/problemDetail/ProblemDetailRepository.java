package com.example.oj.problemDetail;

import com.example.oj.problem.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemDetailRepository extends JpaRepository<ProblemDetail, Long> {

	Optional<ProblemDetail> findById(Long id);

	ProblemDetail save(Problem ProblemDetail);
}
