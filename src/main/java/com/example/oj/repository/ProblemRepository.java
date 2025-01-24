package com.example.oj.repository;

import com.example.oj.entity.Problem;
import com.example.oj.projection.ProblemSimpleProj;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends CrudRepository<Problem, Long> {
	Problem save(Problem problem);


	Problem getById(Long id);

	ProblemSimpleProj findProblemSimpleById(Long id);

	@EntityGraph(attributePaths = "problemDetail")
	Optional<Problem> findById(Long id);


	Page<Problem> findAll(Specification<Problem> specification, Pageable pageable);

	List<Problem> findAll(Specification<Problem> specification);

	//	@Query("SELECT new com.example.oj.DTO.ProblemUserDTO(p.id, p.title, p.difficulty, up.status, up.highestScore) "
	//			+
	//			"FROM Problem p LEFT JOIN UserProblem up ON p.id = up.id.problem.id AND up.id.user.id = :userId "
	//			+ "WHERE (:specification IS NULL OR :specification = true)"
	//	)
	//	Page<ProblemUserDTO> findAllWithStatus(@Param("userId") Long userId,
	//										   @Param("specification") Specification<Problem> specification,
	//										   Pageable pageable);
}
