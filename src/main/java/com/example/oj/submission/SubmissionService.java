package com.example.oj.submission;

import com.example.oj.annotation.AutoFill;
import com.example.oj.codeTester.CodeTester;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
public class SubmissionService {
	@Autowired
	SubmissionRepository submissionRepository;
	@Autowired
	CodeTester codeTester;

	@AutoFill
	public Submission submit(Submission submission) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		submission.setUser(user);
		Submission savedSubmission = submissionRepository.save(submission);
		// Run code testing async.
		runCodeTesting(savedSubmission);
		return savedSubmission;
	}


	@Async("taskExecutor")
	@Transactional
	public void runCodeTesting(Submission submission) {
		try {
			// Update the submission status to RUNNING
			submission.setSubmissionStatus(SubmissionStatus.RUNNING);
			submissionRepository.save(submission);
			Submission testedSubmission = codeTester.test(submission.getProblem(), submission);
			BeanUtils.copyProperties(testedSubmission, submission, "id", "problem", "user", "createTime", "code", "fileName", "language");
			submissionRepository.save(submission);
		} catch (Exception e) {
			submission.setSubmissionStatus(SubmissionStatus.FAILED);
			submission.setSubmissionResultType(SubmissionResultType.JE);
			submission.setMessage("Error on code testing");
			submissionRepository.save(submission);
			log.error("Error on code testing:{}", e.getMessage());
//			throw new RuntimeException(e);
		}
//		return submission;
	}

//	public Long updateAfterTest(Submission submission) {
//		Submission savedSubmission = submissionRepository.save(submission);
//		return savedSubmission.getId();
//	}

	public Submission getById(Long id) {
		Submission submission = submissionRepository.getById(id);
		return submission;
	}

	//    public String getCodeById(Long id) {
	//        String submission = submissionRepository.getCodeById(id);
	//        return submission;
	//    }
	//
	public Page<Submission> getAllSubmissionsByUser(Long id, Pageable pageable) {
		Page<Submission> submissions = submissionRepository.findByUserIdOrderByCreateTimeDesc(id, pageable);
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
}
