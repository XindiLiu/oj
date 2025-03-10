package com.example.oj.specification;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.constant.SubmissionStatus;
import com.example.oj.entity.Submission;
import com.example.oj.constant.ProgrammingLanguage;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class SubmissionSpecification {

	public static Specification<Submission> hasUserId(Long userId) {
		return (Root<Submission> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			if (userId == null) {
				return cb.conjunction();
			}
			return cb.equal(root.get("user").get("id"), userId);
		};
	}

	public static Specification<Submission> hasProblemId(Long problemId) {
		return (Root<Submission> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			if (problemId == null) {
				return cb.conjunction();
			}
			return cb.equal(root.get("problem").get("id"), problemId);
		};
	}

	public static Specification<Submission> hasStatus(SubmissionStatus status) {
		return (Root<Submission> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			if (status == null) {
				return cb.conjunction();
			}
			return cb.equal(root.get("status"), status);
		};
	}

	public static Specification<Submission> hasJudgement(SubmissionResultType judgement) {
		return (Root<Submission> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			if (judgement == null) {
				return cb.conjunction();
			}
			return cb.equal(root.get("judgement"), judgement);
		};
	}

	public static Specification<Submission> hasLanguage(ProgrammingLanguage language) {
		return (Root<Submission> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			if (language == null) {
				return cb.conjunction();
			}
			return cb.equal(root.get("language"), language);
		};
	}
}