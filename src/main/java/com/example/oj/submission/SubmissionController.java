package com.example.oj.submission;

import com.example.oj.common.Result;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.problem.Problem;
import com.example.oj.problem.ProblemService;
import com.example.oj.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubmissionController {
	@Autowired
	SubmissionService submissionService;
	@Autowired
	ProblemService problemService;
	@Autowired
	UserService userService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping("problem/{id}/submit")
	public Result<Submission> submit(@RequestBody SubmissionDTO submissionDTO, @PathVariable Long id) {
		Submission submission = new Submission();
		submission.setLanguage(ProgrammingLanguage.valueOf(submissionDTO.getLanguage()));
		BeanUtils.copyProperties(submissionDTO, submission);

		if (submission.getFileName() == null || submission.getFileName().isEmpty()) {
			submission.setFileName("unnamed." + submission.getLanguage().label);
		}
		Problem problem = problemService.getById(id);
		submission.setProblem(problem);
		submission.setStatus(SubmissionStatus.SUBMITTED);
		Submission submissionResult = submissionService.submit(submission);
		return Result.success(submissionResult);
	}

	@PreAuthorize("hasRole('USER')")
	@PostAuthorize("@userSecurity.isCurrentUser(returnObject.data.getUser().getId())")
	@GetMapping("submission/{id}")
	//	@PreAuthorize("@userSecurity.isCurrentUser(#user.id)")
	public Result getById(@PathVariable Long id) {
		var submission = submissionService.getById(id);
		//        SubmissionSimple submissionSimple = new SubmissionSimple();
		//        BeanUtils.copyProperties(submission, submissionSimple);
		return Result.success(submission);
	}

	@GetMapping("submission/simple/{id}")
	public Result getSimpleById(@PathVariable Long id) {
		var submissionSimple = submissionService.getSimpleById(id);
		return Result.success(submissionSimple);
	}

	//    @GetMapping("submission/{id}/code")
	//    public Result<String> getCodeById(@PathVariable Long id){
	//        String submission = submissionService.getCodeById(id);
	//        return Result.success(submission);
	//    }
	//
	@GetMapping("user/{id}/submissions")
	public Result getAllByUserIdPage(@PathVariable Long id,
									 @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
									 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<SubmissionSimple> submissions = submissionService.getAllSubmissionsByUser(id, pageable);
		return Result.success(submissions);
	}

	//    @GetMapping("submissions")
	//    public Result<Set<Submission>> getAll(){
	//        Set<Submission> submission = submissionService.getAll();
	//        return Result.success(submission);
	//    }

	@GetMapping("problem/{id}/submissions")
	public Result getByProblemId(@PathVariable Long id,
								 @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
								 @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Submission> submission = submissionService.getByProblem(id, pageable);
		return Result.success(submission);
	}

	@GetMapping("problem/time_ranklist/{id}")
	public Result getFastestByProblem(@PathVariable Long id, @RequestParam String language) {
		var submission = submissionService.getFastestByProblem(id,
				ProgrammingLanguage.valueOf(language.toUpperCase()));
		return Result.success(submission);
	}
}
