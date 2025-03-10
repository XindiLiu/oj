package com.example.oj.service;

import com.example.oj.entity.Problem;
import com.example.oj.entity.Submission;
import com.example.oj.entity.User;
import com.example.oj.entity.UserProblemResult;
import com.example.oj.repository.UserProblemResultRepository;
import com.example.oj.repository.UserRepository;
import com.example.oj.utils.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProblemResultService {

	private final UserProblemResultRepository userProblemResultRepository;
	private final UserRepository userRepository;
	@PersistenceContext
	private final EntityManager entityManager;


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

}
