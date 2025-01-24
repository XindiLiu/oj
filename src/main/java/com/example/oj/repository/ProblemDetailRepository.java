package com.example.oj.repository;

import com.example.oj.entity.Problem;
import com.example.oj.entity.ProblemDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemDetailRepository extends JpaRepository<ProblemDetail, Long> {

	Optional<ProblemDetail> findById(Long id);

	ProblemDetail save(Problem ProblemDetail);
}
