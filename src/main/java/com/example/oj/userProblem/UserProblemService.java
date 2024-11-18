package com.example.oj.userProblem;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.problem.Problem;
import com.example.oj.problem.ProblemSearchDTO;
import com.example.oj.problem.ProblemSpecification;
import com.example.oj.problem.ProblemUserDTO;
import com.example.oj.submission.Submission;
import com.example.oj.user.User;
import com.example.oj.user.UserRepository;
import com.example.oj.utils.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserProblemService {
	@Autowired
	UserProblemRepository userProblemRepository;
	@Autowired
	UserRepository userRepository;

	@PersistenceContext
	EntityManager entityManager;

	@Transactional
	public void afterCodeTesting(Submission submission) {
		Problem problem = submission.getProblem();
		User user = submission.getUser();
		UserProblem.UserProblemId userProblemId = new UserProblem.UserProblemId(user, problem);
		Optional<UserProblem> userProblemOptional = userProblemRepository.findById(userProblemId);
//		Optional<UserProblem> userProblemOptional = getUserProblem(user.getId(), problem.getId());
		UserProblem userProblem;
		int old_score = 0;
		if (userProblemOptional.isEmpty()) {
			userProblem = new UserProblem(user, problem, submission);
			userProblemRepository.save(userProblem);
		} else {
			userProblem = userProblemOptional.get();
			old_score = userProblem.getHighestScore();
			if (submission.getScore() > userProblem.getHighestScore()) {
				updateUserProblem(userProblem, submission);
			}
		}
		// Update the user's total score if it increases.
		long scoreDiff = userProblem.getHighestScore() - old_score;
		if (scoreDiff > 0) {
//			long user_old_score = userRepository.getScoreById(user.getId());
//			userRepository.updateScore(user.getId(), user_old_score + scoreDiff);
			userRepository.incrementScore(user.getId(), scoreDiff);
		}
	}

	public Optional<UserProblem> getUserProblem(Long userId, Long problemId) {
		return userProblemRepository.findById_UserIdAndId_ProblemId(userId, problemId);

	}

	private void updateUserProblem(UserProblem userProblem, Submission newSubmission) {
		userProblem.setBestSubmission(newSubmission);
		userProblem.setStatus(newSubmission.getJudgement());
		userProblem.setHighestScore(newSubmission.getScore());
		userProblemRepository.save(userProblem);
	}

//	public Page<ProblemUserDTO> findAllWithStatus(int pageNo, int pageSize, ProblemSearchDTO problemSearchDTO) {
//		User user = SecurityUtil.getCurrentUser();
//		Specification<Problem> queryCondition = ProblemSpecification.buildSpecification(problemSearchDTO);
//		Page<UserProblem> result = userProblemRepository.findAll(queryCondition, PageRequest.of(pageNo - 1, pageSize));
//		return result;
//	}

	public List<ProblemUserDTO> getProblemUserDTOs(Long userId, Specification<Problem> spec) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProblemUserDTO> query = cb.createQuery(ProblemUserDTO.class);
		Root<Problem> problem = query.from(Problem.class);

		// Perform a LEFT JOIN with UserProblem
		Join<Problem, UserProblem> up = problem.join("userProblems", JoinType.LEFT);
		up.on(cb.equal(up.get("id").get("user").get("id"), userId));
		// Build predicates
		Predicate predicate = cb.conjunction();

		if (spec != null) {
			Predicate specPredicate = spec.toPredicate(problem, query, cb);
			if (specPredicate != null) {
				predicate = cb.and(predicate, specPredicate);
			}
		}
		// Set the selection to the ProblemUserDTO constructor
		query.select(cb.construct(
				ProblemUserDTO.class,
				problem.get("id"),
				problem.get("title"),
				problem.get("difficulty"),
				up.get("status"),
				up.get("highestScore")
		)).where(predicate);
		return entityManager.createQuery(query).getResultList();
	}

	public Page<ProblemUserDTO> getProblemUserDTOPage(Long userId, ProblemSearchDTO dto, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProblemUserDTO> query = cb.createQuery(ProblemUserDTO.class);
		Root<Problem> problem = query.from(Problem.class);
		// Perform a LEFT JOIN with UserProblem and apply the userId condition in the join
		Join<Problem, UserProblem> up = problem.join("userProblems", JoinType.LEFT);
		up.on(cb.equal(up.get("id").get("user").get("id"), userId));
		Predicate predicate = cb.conjunction();

		var spec = ProblemSpecification.buildSpecification(dto);
		if (spec != null) {
			Predicate specPredicate = spec.toPredicate(problem, query, cb);
			if (specPredicate != null) {
				predicate = cb.and(predicate, specPredicate);
			}
		}
		predicate = getPredicate(dto, cb, up, predicate);
		// Apply specifications
		query.where(predicate);
		// Set the selection to the ProblemUserDTO constructor
		query.select(cb.construct(
				ProblemUserDTO.class,
				problem.get("id"),
				problem.get("title"),
				problem.get("difficulty"),
				up.get("status"),
				up.get("highestScore")
		));

		// Execute the query with pagination
		List<ProblemUserDTO> resultList = entityManager.createQuery(query)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();

		// Create a count query for total elements
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Problem> countRoot = countQuery.from(Problem.class);
		Join<Problem, UserProblem> countUp = countRoot.join("userProblems", JoinType.LEFT);
		countUp.on(cb.equal(countUp.get("id").get("user").get("id"), userId));

		Predicate countPredicate = cb.conjunction();
		if (spec != null) {
			Predicate specPredicate = spec.toPredicate(countRoot, countQuery, cb);
			if (specPredicate != null) {
				countPredicate = cb.and(countPredicate, specPredicate);
			}
		}
		countPredicate = getPredicate(dto, cb, countUp, countPredicate);

		countQuery.select(cb.countDistinct(countRoot)).where(countPredicate);
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(resultList, pageable, total);
	}

	private Predicate getPredicate(ProblemSearchDTO dto, CriteriaBuilder cb, Join<Problem, UserProblem> countUp, Predicate countPredicate) {
		if (dto.includeAll != null && !dto.includeAll) {
			Predicate statusPredicate = cb.disjunction();
			if (dto.includePassed != null && dto.includePassed) {
				statusPredicate = cb.or(statusPredicate, cb.equal(countUp.get("status"), SubmissionResultType.AC));
			}
			if (dto.includeFailed != null && dto.includeFailed) {
				statusPredicate = cb.or(statusPredicate, cb.and(cb.notEqual(countUp.get("status"), SubmissionResultType.AC), cb.isNotNull(countUp.get("status"))));
			}
			if (dto.includeFailed != null && dto.includeFailed) {
				statusPredicate = cb.or(statusPredicate, cb.isNull(countUp.get("status")));
			}
			countPredicate = cb.and(countPredicate, statusPredicate);
		}
		return countPredicate;
	}

	public Page<ProblemUserDTO> getProblemUserDTOPage2(Long userId, ProblemSearchDTO dto, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProblemUserDTO> query = cb.createQuery(ProblemUserDTO.class);
		Root<Problem> problem = query.from(Problem.class);

		// Perform a LEFT JOIN with UserProblem and apply the userId condition in the join
		Join<Problem, UserProblem> up = problem.join("userProblems", JoinType.LEFT);
		up.on(cb.equal(up.get("id").get("user").get("id"), userId));
		var spec = ProblemSpecification.buildSpecificationWithUser(dto, userId);
		// Apply specifications
		Predicate predicate = cb.conjunction();
		Predicate specPredicate = spec.toPredicate(problem, query, cb);
		predicate = cb.and(predicate, specPredicate);

		query.where(predicate);

		// Set the selection to the ProblemUserDTO constructor
		query.select(cb.construct(
				ProblemUserDTO.class,
				problem.get("id"),
				problem.get("title"),
				problem.get("difficulty"),
				up.get("status"),
				up.get("highestScore")
		));

		// Execute the query with pagination
		List<ProblemUserDTO> resultList = entityManager.createQuery(query)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();

		// Create a count query for total elements
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Problem> countRoot = countQuery.from(Problem.class);
		Join<Problem, UserProblem> countUp = countRoot.join("userProblems", JoinType.LEFT);
		countUp.on(cb.equal(countUp.get("id").get("user").get("id"), userId));

		Predicate countPredicate = cb.conjunction();
		specPredicate = spec.toPredicate(countRoot, countQuery, cb);
		countPredicate = cb.and(countPredicate, specPredicate);

		countQuery.select(cb.countDistinct(countRoot)).where(countPredicate);
		Long total = entityManager.createQuery(countQuery).getSingleResult();
		return new PageImpl<>(resultList, pageable, total);
	}

//	public Page<ProblemUserDTO> getProblemUserDTOPage(Long userId, Specification<Problem> spec, Pageable pageable) {
//		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//		CriteriaQuery<ProblemUserDTO> query = cb.createQuery(ProblemUserDTO.class);
//		Root<Problem> problem = query.from(Problem.class);
//
//		// Perform a LEFT JOIN with UserProblem and apply the userId condition in the join
//		Join<Problem, UserProblem> up = problem.join("userProblems", JoinType.LEFT);
//		up.on(cb.equal(up.get("id").get("user").get("id"), userId));
//
//		// Apply specifications
//		Predicate predicate = cb.conjunction();
//		if (spec != null) {
//			Predicate specPredicate = spec.toPredicate(problem, query, cb);
//			if (specPredicate != null) {
//				predicate = cb.and(predicate, specPredicate);
//			}
//		}
//		query.where(predicate);
//
//		// Set the selection to the ProblemUserDTO constructor
//		query.select(cb.construct(
//				ProblemUserDTO.class,
//				problem.get("id"),
//				problem.get("title"),
//				problem.get("difficulty"),
//				up.get("status"),
//				up.get("highestScore")
//		));
//
//		// Execute the query with pagination
//		List<ProblemUserDTO> resultList = entityManager.createQuery(query)
//				.setFirstResult((int) pageable.getOffset())
//				.setMaxResults(pageable.getPageSize())
//				.getResultList();
//
//		// Create a count query for total elements
//		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//		Root<Problem> countRoot = countQuery.from(Problem.class);
//		Join<Problem, UserProblem> countUp = countRoot.join("userProblems", JoinType.LEFT);
//		countUp.on(cb.equal(countUp.get("id").get("user").get("id"), userId));
//
//		Predicate countPredicate = cb.conjunction();
//		if (spec != null) {
//			Predicate specPredicate = spec.toPredicate(countRoot, countQuery, cb);
//			if (specPredicate != null) {
//				countPredicate = cb.and(countPredicate, specPredicate);
//			}
//		}
//		countQuery.select(cb.countDistinct(countRoot)).where(countPredicate);
//		Long total = entityManager.createQuery(countQuery).getSingleResult();
//		return new PageImpl<>(resultList, pageable, total);
//	}


}
