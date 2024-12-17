package com.example.oj.submission;

import com.example.oj.codeTester.CodeTester;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.exception.CodeTesterUnavailableException;
import com.example.oj.exception.CodeTestingException;
import com.example.oj.problem.ProblemRepository;
import com.example.oj.problem.ProblemSimplePorj;
import com.example.oj.testcase.TestCase;
import com.example.oj.user.User;
import com.example.oj.user.UserRepository;
import com.example.oj.user.UserSimpleProj;
import com.example.oj.userProblem.UserProblemResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.example.oj.utils.SecurityUtil;

@Service
@Slf4j
public class SubmissionService {
	@Autowired
	SubmissionRepository submissionRepository;
	@Autowired
	ProblemRepository problemRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CodeTester codeTester;
	@Autowired
	UserProblemResultService userProblemResultService;

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
		}

		submissionRepository.save(submission);
		userProblemResultService.afterCodeTesting(submission);
	}

	public Submission submit(Submission submission) throws CodeTesterUnavailableException, CodeTestingException {
		User user = SecurityUtil.getCurrentUser();
		submission.setUser(user);
		submission.setStatus(SubmissionStatus.RUNNING);
		Submission savedSubmission = submissionRepository.save(submission);
		// Run code testing async. 
		codeTester.test(submission.getProblem(), savedSubmission, this::afterCodeTesting);
		return savedSubmission;
	}

	//	@Transactional
	//	public void runCodeTesting(Submission submission) {
	//		try {
	//			// Update the submission status to RUNNING
	//			submission.setStatus(SubmissionStatus.RUNNING);
	//			submissionRepository.save(submission);
	//			Submission testedSubmission = codeTester.test(submission.getProblem(), submission);
	//			BeanUtils.copyProperties(testedSubmission, submission, "id", "problem", "user", "createTime", "code",
	//					"fileName", "language");
	//			submissionRepository.save(submission);
	//		} catch (Exception e) {
	//			submission.setStatus(SubmissionStatus.FAILED);
	//			submission.setJudgement(SubmissionResultType.JE);
	//			submission.setMessage("Error on code testing");
	//			submissionRepository.save(submission);
	//			log.error("Error on code testing:{}", e.getMessage());
	//			//			throw new RuntimeException(e);
	//		}
	//		//		return submission;
	//	}

	public SubmissionInfo getById(Long id) {
		SubmissionInfo submission = submissionRepository.findSubmissionInfoById(id);
		//		Submission submission = submissionRepository.getById(id);
		return submission;
	}

	@Transactional
	public SubmissionSimple getSimpleById(Long id) {
		SubmissionSimple submissionSimple = submissionRepository.findProblemSimpleById(id);
		ProblemSimplePorj problemSimplePorj = problemRepository
				.findProblemSimpleById(submissionSimple.getProblem().getId());
		UserSimpleProj userSimpleProj = userRepository.findUserSimpleById(submissionSimple.getUser().getId());
		submissionSimple.getProblem().setTitle(problemSimplePorj.getTitle());
		submissionSimple.getUser().setName(userSimpleProj.getDisplayName());
		return submissionSimple;
	}

	//    public String getCodeById(Long id) {
	//        String submission = submissionRepository.getCodeById(id);
	//        return submission; 
	//    }
	//
	public Page<SubmissionSimple> getAllSubmissionsByUser(Long id, Pageable pageable) {
		Page<SubmissionSimple> submissions = submissionRepository.findSimpleByUserIdOrderByCreateTimeDesc(id, pageable);
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
	public List<SubmissionSimple> getFastestByProblem(Long id, ProgrammingLanguage language) {
		//		List<Submission> submission = submissionRepository.findByProblemIdAndStatusAndJudgementOrderByRunTimeMs(id, SubmissionStatus.FINISHED, SubmissionResultType.AC, Sort.by("run_time").ascending(), Limit.of(limit));
		List<SubmissionSimple> submission = submissionRepository
				.findFastes(id, language,
						//						SubmissionStatus.FINISHED, SubmissionResultType.AC, ScrollPosition.offset())
						SubmissionStatus.FINISHED, SubmissionResultType.AC, PageRequest.of(0, 10))
				//				.toList()
				;
		return submission;
	}

}
