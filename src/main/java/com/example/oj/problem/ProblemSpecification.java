package com.example.oj.problem;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.userProblem.UserProblemResult;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.example.oj.constant.ProblemVisibility;

public class ProblemSpecification {
	public static Specification<Problem> hasTitle(String title) {
		return (root, query, criteriaBuilder) -> {
			if (title == null || title.trim().isEmpty()) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.like(
					criteriaBuilder.lower(root.get("title")),
					"%" + title.toLowerCase() + "%"
			);
		};
	}

	public static Specification<Problem> hasCreateUser(Long createUser) {
		return (root, query, criteriaBuilder) -> {
			if (createUser == null) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.equal(root.get("createUser"), createUser);
		};
	}

	public static Specification<Problem> hasDifficultyLowerBound(Integer difficultyLowerBound) {
		return (root, query, criteriaBuilder) -> {
			if (difficultyLowerBound == null) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.greaterThanOrEqualTo(root.get("difficulty"), difficultyLowerBound);
		};
	}

	public static Specification<Problem> hasDifficultyUpperBound(Integer difficultyUpperBound) {
		return (root, query, criteriaBuilder) -> {
			if (difficultyUpperBound == null) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.lessThanOrEqualTo(root.get("difficulty"), difficultyUpperBound);
		};
	}

	public static Specification<Problem> hasVisibility(ProblemVisibility visibility) {
		return (root, query, criteriaBuilder) -> {
			if (visibility == null) {
				return criteriaBuilder.conjunction();
			}
			return criteriaBuilder.equal(root.get("visibility"), visibility);
		};
	}

	public static Specification<Problem> buildSpecification(ProblemSearchDTO dto) {
		if (dto == null) {
			return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
		}
		return Specification
				.where(hasTitle(dto.getTitle()))
				.and(hasCreateUser(dto.getCreateUser()))
				.and(hasDifficultyLowerBound(dto.getMinDifficulty()))
				.and(hasDifficultyUpperBound(dto.getMaxDifficulty()))
				.and(hasVisibility(dto.getVisibility()));
	}

	public static Specification<Problem> buildSpecificationWithUser(ProblemSearchDTO dto, Long userId) {
		Specification<Problem> problemSpec = buildSpecification(dto);
		Specification<Problem> problemSpecWithUser = (root, query, cb) -> {
			Join<Problem, UserProblemResult> up = root.join("userProblems", JoinType.LEFT);
			up.on(cb.equal(up.get("id").get("user").get("id"), userId));
			Predicate predicate = cb.conjunction();
			Predicate specPredicate = problemSpec.toPredicate(root, query, cb);
			predicate = cb.and(predicate, specPredicate);
			if (dto.getIncludeAll() != null && !dto.getIncludeAll()) {
				Predicate statusPredicate = cb.disjunction();
				if (dto.getIncludePassed() != null && dto.getIncludePassed()) {
					statusPredicate = cb.or(statusPredicate, cb.equal(up.get("status"), SubmissionResultType.AC));
				}
				if (dto.getIncludePassed() != null && dto.getIncludeFailed()) {
					statusPredicate = cb.or(statusPredicate, cb.and(cb.notEqual(up.get("status"), SubmissionResultType.AC), cb.isNotNull(up.get("status"))));
				}
				if (dto.getIncludePassed() != null && dto.getIncludeNotTried()) {
					statusPredicate = cb.or(statusPredicate, cb.isNull(up.get("status")));
				}
				predicate = cb.and(predicate, statusPredicate);
			}
			return predicate;
		};
		return problemSpecWithUser;
	}

}