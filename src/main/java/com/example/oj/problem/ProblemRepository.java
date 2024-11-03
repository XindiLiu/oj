package com.example.oj.problem;

import com.example.oj.annotation.AutoFill;
import com.example.oj.problem.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProblemRepository extends CrudRepository<Problem, Long> {
	Problem save(Problem problem);

	Problem getById(Long id);

	Page<Problem> findAll(Specification<Problem> specification, Pageable pageable);

	List<Problem> findAll(Specification<Problem> specification);


}
