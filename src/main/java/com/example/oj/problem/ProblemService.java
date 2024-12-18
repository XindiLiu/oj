package com.example.oj.problem;

import com.example.oj.exception.IdNotFoundException;
import com.example.oj.problemDetail.ProblemDetail;
import com.example.oj.problemDetail.ProblemDetailRepository;
import com.example.oj.user.User;
import com.example.oj.userProblem.UserProblemResult;
import com.example.oj.userProblem.UserProblemResultService;
import com.example.oj.utils.BeanCopyUtils;
import com.example.oj.utils.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {
	@Autowired
	ProblemRepository problemRepository;
	@Autowired
	ProblemDetailRepository detailRepository;
	@Autowired
	UserProblemResultService userProblemResultService;
	@PersistenceContext
	EntityManager entityManager;

	@Transactional
	public Problem getById(Long id) {
		Problem problem = problemRepository.getById(id);
		return problem;
	}

	public ProblemDetail getDetailById(Long id) {
		ProblemDetail detail = detailRepository.getById(id);
		return detail;
	}

	@Transactional
	public ProblemDetail add(Problem problem) {
		User user = SecurityUtil.getCurrentUser();
		if (problem.getId() != null) {
			problem.setCreateUser(user.getId());
		}
		problem.setUpdateUser(user.getId());
		return detailRepository.save(problem.getProblemDetail());
	}

	@Transactional
	public ProblemDetail update(Long id, ProblemCreateDTO problemDto) throws IdNotFoundException {
		User user = SecurityUtil.getCurrentUser();

		ProblemDetail problemDetail = detailRepository.findById(id).orElseThrow(() -> new IdNotFoundException(Problem.class, id));
		if (problemDetail == null) {
			return null;
		}
		BeanCopyUtils.copyNonNullSrcProperties(problemDto, problemDetail);
		BeanCopyUtils.copyNonNullSrcProperties(problemDto, problemDetail.getProblem());
		problemDetail.getProblem().setUpdateUser(user.getId());
		ProblemDetail savedProblem = detailRepository.save(problemDetail);
		return savedProblem;
	}


	public ProblemSimpleProj getSimpleById(Long id) {
		return problemRepository.findProblemSimpleById(id);
	}

	public Page<ProblemUserDTO> getPagedProblem(int pageNo, int pageSize, ProblemSearchDTO problemSearchDTO) {
		var queryCondition = ProblemSpecification.buildSpecification(problemSearchDTO);
		var problemResult = problemRepository.findAll(queryCondition, PageRequest.of(pageNo, pageSize));
		var result = problemResult.map((p) -> new ProblemUserDTO(p));
		return result;
	}

	public Page<ProblemUserDTO> getPagedProblemWithUser(int pageNumber, int pageSize,
														ProblemSearchDTO problemSearchDTO) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		User user = SecurityUtil.getCurrentUser();
		if (user == null) {
			return getPagedProblem(pageNumber, pageSize, problemSearchDTO);
		}
		Long userId = user.getId();

		// Fetch the paged data
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProblemUserDTO> query = cb.createQuery(ProblemUserDTO.class);
		Root<Problem> problem = query.from(Problem.class);
		Join<Problem, UserProblemResult> upJoin = problem.join("userProblemResults", JoinType.LEFT);
		upJoin.on(cb.equal(upJoin.get("id").get("user").get("id"), userId));
		Predicate predicate = ProblemSpecification.buildSpecificationWithUser(problemSearchDTO, userId)
				.toPredicate(problem, query, cb);
		query.where(predicate);
		query.select(cb.construct(
				ProblemUserDTO.class,
				problem.get("id"),
				problem.get("title"),
				problem.get("difficulty"),
				upJoin.get("status"),
				upJoin.get("highestScore")));
		List<ProblemUserDTO> resultList = entityManager.createQuery(query)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();

		// Count query for PageImpl
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Problem> countRoot = countQuery.from(Problem.class);
		Join<Problem, UserProblemResult> countJoin = countRoot.join("userProblemResults", JoinType.LEFT);
		countJoin.on(cb.equal(countJoin.get("id").get("user").get("id"), userId));

		Predicate countPredicate = ProblemSpecification.buildSpecificationWithUser(problemSearchDTO, userId)
				.toPredicate(countRoot, countQuery, cb);
		countQuery.select(cb.countDistinct(countRoot)).where(countPredicate);
		Long total = entityManager.createQuery(countQuery).getSingleResult();
		return new PageImpl<>(resultList, pageable, total);
	}
}
