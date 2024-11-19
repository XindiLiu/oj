package com.example.oj.problem;

import com.example.oj.problemDetail.ProblemDetail;
import com.example.oj.problemDetail.ProblemDetailRepository;
import com.example.oj.user.User;
import com.example.oj.userProblem.UserProblem;
import com.example.oj.userProblem.UserProblemService;
import com.example.oj.utils.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProblemService {
	@Autowired
	ProblemRepository problemRepository;
	@Autowired
	ProblemDetailRepository detailRepository;
	@Autowired
	UserProblemService userProblemService;
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
	public void save(Problem problem) {
		User user = SecurityUtil.getCurrentUser();
		problem.setCreateUser(user.getId());
		problem.setUpdateUser(user.getId());
		problemRepository.save(problem);
	}

	@Transactional
	public void update(Problem problem) {
		User user = SecurityUtil.getCurrentUser();
		problem.setUpdateUser(user.getId());
		problemRepository.save(problem);
	}

//	@Transactional
//	public void save(ProblemDetail problemDetail) {
//		detailRepository.save(problemDetail);
////        Problem problem = new Problem();
////        problem.setId(problemDetail.getId());
////        problemRepository.save(problem);
//	}

	public Page<Problem> page(int pageNo, int pageSize, ProblemSearchDTO problemSearchDTO) {
		Specification<Problem> queryCondition = ProblemSpecification.buildSpecification(problemSearchDTO);
		try {
			var result = problemRepository.findAll(queryCondition,
					PageRequest.of(pageNo - 1, pageSize));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void uploadTestData(MultipartFile file) {
	}

	public ProblemSimplePorj getSimpleById(Long id) {
		return problemRepository.findProblemSimpleById(id);
	}

//	public List<ProblemUserDTO> pageWithStatus(ProblemSearchDTO problemSearchDTO) {
//		User user = SecurityUtil.getCurrentUser();
//		Specification<Problem> queryCondition = ProblemSpecification.buildSpecification(problemSearchDTO);
//		List<ProblemUserDTO> result = userProblemService.getProblemUserDTOs(user.getId(), queryCondition);
//		return result;
//	}
//
//	public Page<Problem> pageWithStatus(int pageNo, int pageSize, ProblemSearchDTO problemSearchDTO) {
//		User user = SecurityUtil.getCurrentUser();
//		Page<Problem> result = problemRepository.findAll(ProblemSpecification.buildSpecificationWithUser(problemSearchDTO, user.getId()), PageRequest.of(pageNo, pageSize));
//		return result;
//	}


	//	public Page<ProblemUserDTO> getPagedProblemUserDTOs(int pageNumber, int pageSize, ProblemSearchDTO problemSearchDTO) {
//		// Convert pageNumber to zero-based index
//		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
//		// Build the specification based on search criteria
//		Specification<Problem> spec = ProblemSpecification.buildSpecification(problemSearchDTO);
//		// Retrieve the current user's ID (implement based on your security context)
//		User user = SecurityUtil.getCurrentUser();
//		// Fetch the paged data
////		Page<ProblemUserDTO> problemUserDTOPage = userProblemService.getProblemUserDTOPage(user.getId(), spec, pageable);
//		Page<ProblemUserDTO> problemUserDTOPage = userProblemService.getProblemUserDTOPage2(user.getId(), problemSearchDTO, pageable);
//		return problemUserDTOPage;
//	}
	public Page<ProblemUserDTO> getPagedProblem(int pageNo, int pageSize, ProblemSearchDTO problemSearchDTO) {
		var queryCondition = ProblemSpecification.buildSpecification(problemSearchDTO);
		var problemResult = problemRepository.findAll(queryCondition, PageRequest.of(pageNo - 1, pageSize));
		var result = problemResult.map((p) -> new ProblemUserDTO(p));
		return result;
	}


	public Page<ProblemUserDTO> getPagedProblemWithUser(int pageNumber, int pageSize, ProblemSearchDTO problemSearchDTO) {
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
		User user = SecurityUtil.getCurrentUser();
		if (user == null) {
			return getPagedProblem(pageNumber, pageSize, problemSearchDTO);
		}
		Long userId = user.getId();

		// Fetch the paged data
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProblemUserDTO> query = cb.createQuery(ProblemUserDTO.class);
		Root<Problem> problem = query.from(Problem.class);
		Join<Problem, UserProblem> upJoin = problem.join("userProblems", JoinType.LEFT);
		upJoin.on(cb.equal(upJoin.get("id").get("user").get("id"), userId));
		Predicate predicate = ProblemSpecification.buildSpecificationWithUser(problemSearchDTO, userId).toPredicate(problem, query, cb);
		query.where(predicate);
		query.select(cb.construct(
				ProblemUserDTO.class,
				problem.get("id"),
				problem.get("title"),
				problem.get("difficulty"),
				upJoin.get("status"),
				upJoin.get("highestScore")
		));
		List<ProblemUserDTO> resultList = entityManager.createQuery(query)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();

		// Count query for PageImpl
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Problem> countRoot = countQuery.from(Problem.class);
		Join<Problem, UserProblem> countJoin = countRoot.join("userProblems", JoinType.LEFT);
		countJoin.on(cb.equal(countJoin.get("id").get("user").get("id"), userId));

		Predicate countPredicate = ProblemSpecification.buildSpecificationWithUser(problemSearchDTO, userId).toPredicate(countRoot, countQuery, cb);
		countQuery.select(cb.countDistinct(countRoot)).where(countPredicate);
		Long total = entityManager.createQuery(countQuery).getSingleResult();
		return new PageImpl<>(resultList, pageable, total);
	}
}
