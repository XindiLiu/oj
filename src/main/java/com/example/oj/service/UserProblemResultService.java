package com.example.oj.service;

import com.example.oj.common.Result;
import com.example.oj.entity.Problem;
import com.example.oj.entity.Submission;
import com.example.oj.entity.User;
import com.example.oj.entity.UserProblemResult;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.repository.UserProblemResultRepository;
import com.example.oj.repository.UserRepository;
import com.example.oj.utils.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserProblemResultService {

	private final UserProblemResultRepository userProblemResultRepository;
	private final UserRepository userRepository;
	@PersistenceContext
	private final EntityManager entityManager;

	public UserProblemResultService(UserProblemResultRepository userProblemResultRepository,
									UserRepository userRepository, EntityManager entityManager) {
		this.userProblemResultRepository = userProblemResultRepository;
		this.userRepository = userRepository;
		this.entityManager = entityManager;
	}

	/*
	 * Update UserProblemResult after evaluation of a submission.
	 */
	@Transactional
	public void afterCodeTesting(Submission submission) {
		Problem problem = submission.getProblem();
		User user = submission.getUser();
		UserProblemResult.UserProblemId userProblemId = new UserProblemResult.UserProblemId(user, problem);
		Optional<UserProblemResult> userProblemOptional = userProblemResultRepository.findById(userProblemId);
		UserProblemResult userProblemResult;
		int old_score = 0;
		if (userProblemOptional.isEmpty()) {
			userProblemResult = new UserProblemResult(user, problem, submission);
			userProblemResultRepository.save(userProblemResult);
		} else {
			userProblemResult = userProblemOptional.get();
			old_score = userProblemResult.getHighestScore();
			if (submission.getScore() > userProblemResult.getHighestScore()) {
				updateUserProblem(userProblemResult, submission);
			}
		}
		// Update the user's total score if it increases.
		long scoreDiff = userProblemResult.getHighestScore() - old_score;
		if (scoreDiff > 0) {
			userRepository.incrementScore(user.getId(), scoreDiff);
		}
	}

	public UserProblemResult getUserProblem(Long problemId) {
		Long userId = SecurityUtil.getCurrentUser().getId();
		UserProblemResult userProblemResult = userProblemResultRepository.findById_UserIdAndId_ProblemId(userId, problemId).orElse(null);
		return userProblemResult;
	}

	private void updateUserProblem(UserProblemResult userProblemResult, Submission newSubmission) {
		userProblemResult.setBestSubmission(newSubmission);
		userProblemResult.setStatus(newSubmission.getJudgement());
		userProblemResult.setHighestScore(newSubmission.getScore());
		userProblemResultRepository.save(userProblemResult);
	}

	/*
		public List<ProblemUserDTO> getProblemUserDTOs(Long userId, Specification<Problem> spec) {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<ProblemUserDTO> query = cb.createQuery(ProblemUserDTO.class);
			Root<Problem> problem = query.from(Problem.class);
	
			// Perform a LEFT JOIN with UserProblem
			Join<Problem, UserProblemResult> up = problem.join("userProblemResults", JoinType.LEFT);
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
					up.get("highestScore"))).where(predicate);
			return entityManager.createQuery(query).getResultList();
		}
	
		public Page<ProblemUserDTO> getProblemUserDTOPage(Long userId, ProblemSearchDTO dto, Pageable pageable) {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<ProblemUserDTO> query = cb.createQuery(ProblemUserDTO.class);
			Root<Problem> problem = query.from(Problem.class);
			// Perform a LEFT JOIN with UserProblem and apply the userId condition in the join
			Join<Problem, UserProblemResult> up = problem.join("userProblemResults", JoinType.LEFT);
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
					up.get("highestScore")));
	
			// Execute the query with pagination
			List<ProblemUserDTO> resultList = entityManager.createQuery(query)
					.setFirstResult((int) pageable.getOffset())
					.setMaxResults(pageable.getPageSize())
					.getResultList();
	
			// Create a count query for total elements
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Problem> countRoot = countQuery.from(Problem.class);
			Join<Problem, UserProblemResult> countUp = countRoot.join("userProblemResults", JoinType.LEFT);
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
	
		private Predicate getPredicate(ProblemSearchDTO dto, CriteriaBuilder cb, Join<Problem, UserProblemResult> countUp,
									   Predicate countPredicate) {
			if (dto.includeAll != null && !dto.includeAll) {
				Predicate statusPredicate = cb.disjunction();
				if (dto.includePassed != null && dto.includePassed) {
					statusPredicate = cb.or(statusPredicate, cb.equal(countUp.get("status"), SubmissionResultType.AC));
				}
				if (dto.includeFailed != null && dto.includeFailed) {
					statusPredicate = cb.or(statusPredicate,
							cb.and(cb.notEqual(countUp.get("status"), SubmissionResultType.AC),
									cb.isNotNull(countUp.get("status"))));
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
			Join<Problem, UserProblemResult> up = problem.join("userProblemResults", JoinType.LEFT);
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
					up.get("highestScore")));
	
			// Execute the query with pagination
			List<ProblemUserDTO> resultList = entityManager.createQuery(query)
					.setFirstResult((int) pageable.getOffset())
					.setMaxResults(pageable.getPageSize())
					.getResultList();
	
			// Create a count query for total elements
			CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
			Root<Problem> countRoot = countQuery.from(Problem.class);
			Join<Problem, UserProblemResult> countUp = countRoot.join("userProblemResults", JoinType.LEFT);
			countUp.on(cb.equal(countUp.get("id").get("user").get("id"), userId));
	
			Predicate countPredicate = cb.conjunction();
			specPredicate = spec.toPredicate(countRoot, countQuery, cb);
			countPredicate = cb.and(countPredicate, specPredicate);
	
			countQuery.select(cb.countDistinct(countRoot)).where(countPredicate);
			Long total = entityManager.createQuery(countQuery).getSingleResult();
			return new PageImpl<>(resultList, pageable, total);
		}
	*/

}
