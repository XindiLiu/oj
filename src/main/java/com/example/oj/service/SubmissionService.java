package com.example.oj.service;

import com.example.oj.codeTester.CodeTester;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.entity.Problem;
import com.example.oj.entity.Submission;
import com.example.oj.repository.ProblemRepository;
import com.example.oj.projection.ProblemSimpleProj;
import com.example.oj.repository.SubmissionRepository;
import com.example.oj.projection.SubmissionFullDTOProj;
import com.example.oj.DTO.SubmissionSimpleDTO;
import com.example.oj.constant.SubmissionStatus;
import com.example.oj.entity.TestCase;
import com.example.oj.entity.User;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.repository.UserRepository;
import com.example.oj.projection.UserSimpleProj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.oj.utils.SecurityUtil;

@Service
@Slf4j
public class SubmissionService {
	private final SubmissionRepository submissionRepository;
	private final ProblemRepository problemRepository;
	private final UserRepository userRepository;
	private final CodeTester codeTester;
	private final UserProblemResultService userProblemResultService;

	public SubmissionService(SubmissionRepository submissionRepository, ProblemRepository problemRepository,
							 UserRepository userRepository, CodeTester codeTester, UserProblemResultService userProblemResultService) {
		this.submissionRepository = submissionRepository;
		this.problemRepository = problemRepository;
		this.userRepository = userRepository;
		this.codeTester = codeTester;
		this.userProblemResultService = userProblemResultService;
	}

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
		//		Submission submission = submissionRepository.getById(id);
		return submission;
	}

	@Transactional
	public SubmissionSimpleDTO getSimpleById(Long id) {
		SubmissionSimpleDTO submissionSimpleDTO = submissionRepository.findProblemSimpleById(id);
		ProblemSimpleProj problemSimpleProj = problemRepository
				.findProblemSimpleById(submissionSimpleDTO.getProblem().getId());
		UserSimpleProj userSimpleProj = userRepository.findUserSimpleById(submissionSimpleDTO.getUser().getId());
		submissionSimpleDTO.getProblem().setTitle(problemSimpleProj.getTitle());
		submissionSimpleDTO.getUser().setDisplayName(userSimpleProj.getDisplayName());
		return submissionSimpleDTO;
	}

	//    public String getCodeById(Long id) {
	//        String submission = submissionRepository.getCodeById(id);
	//        return submission; 
	//    }
	//
	public Page<SubmissionSimpleDTO> getAllSubmissionsByUser(Long id, Pageable pageable) {
		Page<SubmissionSimpleDTO> submissions = submissionRepository.findSimpleByUserIdOrderByCreateTimeDesc(id,
				pageable);
		return submissions;
	}

	//
	//    public Set<Submission> getAll() {
	//        Set<Submission> submissions = submissionRepository.find();
	//        return submissions;
	//    }

	public Page<Submission> getByProblem(Long id, Pageable pageable) {
		Page<Submission> submission = submissionRepository.findByProblemIdOrderByCreateTimeDesc(id, pageable);
		return submission;
	}

	//	public List<Submission> get10FastestByProblem(Long id, SubmissionStatus status, SubmissionResultType judgement, Pageable pageable) {
	//		Window<Submission> submissionWindow = submissionRepository.findFirst10ByProblemIdAndStatusAndJudgementOrderByRunTimeMs(id, status, judgement, ScrollPosition.offset());
	//		List<Submission> submission = submissionWindow.toList();
	//		return submission;
	//
	//	}
	public List<SubmissionSimpleDTO> getFastestByProblem(Long id, ProgrammingLanguage language) {
		//		List<Submission> submission = submissionRepository.findByProblemIdAndStatusAndJudgementOrderByRunTimeMs(id, SubmissionStatus.FINISHED, SubmissionResultType.AC, Sort.by("run_time").ascending(), Limit.of(limit));
		List<SubmissionSimpleDTO> submission = submissionRepository
				.findFastes(id, language,
						//						SubmissionStatus.FINISHED, SubmissionResultType.AC, ScrollPosition.offset())
						SubmissionStatus.FINISHED, SubmissionResultType.AC, PageRequest.of(0, 10))
				//				.toList()
				;
		return submission;
	}

}
