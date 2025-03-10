package com.example.oj.service;

import com.example.oj.codetester.CodeTester;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.entity.Problem;
import com.example.oj.entity.Submission;
import com.example.oj.repository.ProblemRepository;
import com.example.oj.dto.ProblemSimpleProj;
import com.example.oj.repository.SubmissionRepository;
import com.example.oj.dto.SubmissionFullDTOProj;
import com.example.oj.dto.SubmissionSimpleDTO;
import com.example.oj.constant.SubmissionStatus;
import com.example.oj.entity.TestCase;
import com.example.oj.entity.User;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.repository.UserRepository;
import com.example.oj.dto.UserSimpleProj;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.oj.utils.SecurityUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionService {
	private final SubmissionRepository submissionRepository;
	private final ProblemRepository problemRepository;
	private final UserRepository userRepository;
	private final CodeTester codeTester;
	private final UserProblemResultService userProblemResultService;

	@Transactional
	public void afterCodeTesting(Submission submission) {
		// Calculate the score if testing finished normally.
		if (submission.getStatus() == SubmissionStatus.FINISHED) {
			double score = 0;
			double weightSum = 0;
			int i = 0;
			for (TestCase testCase : submission.getProblem().getTestCases()) {
				if (i < submission.getNumPassedCases()) {
					score += testCase.getWeight();
				}
				weightSum += testCase.getWeight();
				i++;
			}
			submission.setScore(Integer.valueOf((int) ((score / weightSum) * 100)));
			submissionRepository.save(submission);
			userProblemResultService.afterCodeTesting(submission);
		} else {
			submissionRepository.save(submission);
		}
	}

	public Submission submit(Submission submission, Long problemId) throws IdNotFoundException {
		submission.setStatus(SubmissionStatus.SUBMITTED);
		if (submission.getFileName() == null || submission.getFileName().isEmpty()) {
			submission.setFileName("unnamed." + submission.getLanguage().fileExtension);
		}
		Problem problem = problemRepository.findById(problemId)
				.orElseThrow(() -> new IdNotFoundException(Problem.class, problemId));
		submission.setProblem(problem);
		User user = SecurityUtil.getCurrentUser();
		submission.setUser(user);
		submission.setStatus(SubmissionStatus.RUNNING);
		Submission savedSubmission = submissionRepository.save(submission);
		// Run code testing async. 
		codeTester.test(submission.getProblem(), savedSubmission, this::afterCodeTesting);
		return savedSubmission;
	}

	public SubmissionFullDTOProj getById(Long id) {
		SubmissionFullDTOProj submission = submissionRepository.findSubmissionInfoById(id);
		return submission;
	}


	public SubmissionSimpleDTO getSimpleById(Long id) {
		SubmissionSimpleDTO submissionSimpleDTO = submissionRepository.findProblemSimpleById(id);
		ProblemSimpleProj problemSimpleProj = problemRepository
				.findProblemSimpleById(submissionSimpleDTO.getProblem().getId());
		UserSimpleProj userSimpleProj = userRepository.findUserSimpleById(submissionSimpleDTO.getUser().getId());
		submissionSimpleDTO.getProblem().setTitle(problemSimpleProj.getTitle());
		submissionSimpleDTO.getUser().setDisplayName(userSimpleProj.getDisplayName());
		return submissionSimpleDTO;
	}

	public Page<SubmissionSimpleDTO> getAllSubmissionsByUser(Long id, Pageable pageable) {
		Page<SubmissionSimpleDTO> submissions = submissionRepository.findSimpleByUserIdOrderByCreateTimeDesc(id,
				pageable);
		return submissions;
	}

	public Page<Submission> getByProblem(Long id, Pageable pageable) {
		Page<Submission> submission = submissionRepository.findByProblemIdOrderByCreateTimeDesc(id, pageable);
		return submission;
	}

	public List<SubmissionSimpleDTO> getFastestByProblem(Long id, ProgrammingLanguage language) {
		List<SubmissionSimpleDTO> submission = submissionRepository
				.findFastes(id, language,
						SubmissionStatus.FINISHED, SubmissionResultType.AC, PageRequest.of(0, 10));
		return submission;
	}

}
