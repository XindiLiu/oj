package com.example.oj.controller;

import com.example.oj.DTO.SubmissionDTO;
import com.example.oj.DTO.SubmissionSimpleDTO;
import com.example.oj.common.Result;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionStatus;
import com.example.oj.entity.Submission;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.entity.Problem;
import com.example.oj.projection.SubmissionFullDTOProj;
import com.example.oj.service.ProblemService;
import com.example.oj.service.SubmissionService;
import com.example.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class SubmissionController {

	private final SubmissionService submissionService;
	private final ProblemService problemService;
	private final UserService userService;

	public SubmissionController(SubmissionService submissionService,
			ProblemService problemService,
			UserService userService) {
		this.submissionService = submissionService;
		this.problemService = problemService;
		this.userService = userService;
	}

	@PreAuthorize("hasRole('USER')") // Must be logged in to submit a submission.
	@PostMapping("problem/{id}/submit")
	/*
	 * Submit a submission for a problem.
	 */
	public Result<Submission> submit(@RequestBody SubmissionDTO submissionDTO, @PathVariable Long id)
			throws IdNotFoundException {
		Submission submission = new Submission();
		submission.setLanguage(ProgrammingLanguage.valueOf(submissionDTO.getLanguage()));
		BeanUtils.copyProperties(submissionDTO, submission);

		if (submission.getFileName() == null || submission.getFileName().isEmpty()) {
			submission.setFileName("unnamed." + submission.getLanguage().fileExtension);
		}
		Problem problem = problemService.getById(id);
		if (problem == null) {
			throw new IdNotFoundException(Problem.class, id);
		}
		submission.setProblem(problem);
		submission.setStatus(SubmissionStatus.SUBMITTED);
		Submission submissionResult = null;
		submissionResult = submissionService.submit(submission);
		return Result.success(submissionResult);
	}

	@PreAuthorize("hasRole('USER')")
	@PostAuthorize("@userSecurity.isCurrentUser(returnObject.data.getUser().getId())")
	@GetMapping("submission/{id}")
	/*
	 * Get result of a submission, including the code. Users can only see code of their own submissions.
	 * TODO: Return without code if not current user.
	 */
	public Result getById(@PathVariable Long id) {
		SubmissionFullDTOProj submission = submissionService.getById(id);
		return Result.success(submission);
	}

	@GetMapping("submission/simple/{id}")
	/*
	 * Get result of a submission, excluding the code.
	 */
	public Result getSimpleById(@PathVariable Long id) {
		var submissionSimple = submissionService.getSimpleById(id);
		return Result.success(submissionSimple);
	}

	@GetMapping("user/{id}/submissions")
	public Result getAllByUserIdPage(@PathVariable Long id,
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<SubmissionSimpleDTO> submissions = submissionService.getAllSubmissionsByUser(id, pageable);
		return Result.success(submissions);
	}

	//	@GetMapping("problem/{id}/submissions")
	//	public Result getByProblemId(@PathVariable Long id,
	//								 @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
	//								 @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
	//		Pageable pageable = PageRequest.of(page, size);
	//		Page<Submission> submission = submissionService.getByProblem(id, pageable);
	//		return Result.success(submission);
	//	}

	@GetMapping("problem/time_ranklist/{id}")
	public Result getFastestByProblem(@PathVariable Long id, @RequestParam String language) {
		var submission = submissionService.getFastestByProblem(id,
				ProgrammingLanguage.valueOf(language.toUpperCase()));
		return Result.success(submission);
	}
}
