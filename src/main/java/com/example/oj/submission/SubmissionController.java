package com.example.oj.submission;

import com.example.oj.common.Result;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.problem.Problem;
import com.example.oj.problem.ProblemServiceImpl;
import com.example.oj.user.User;
import com.example.oj.user.UserServiceImpl;
import com.example.oj.utils.BaseContext;
import com.example.oj.utils.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class SubmissionController {
	@Autowired
	SubmissionService submissionService;
	@Autowired
	ProblemServiceImpl problemService;
	@Autowired
	UserServiceImpl userService;

	@PostMapping("problem/{id}/submit")
	public Result<Submission> submit(@RequestBody SubmissionDTO submissionDTO, @PathVariable Long id) {
		Submission submission = new Submission();
		submission.setLanguage(ProgrammingLanguage.valueOf(submissionDTO.getLanguage()));
		BeanUtils.copyProperties(submissionDTO, submission);

		if (submission.getFileName() == null) {
			submission.setFileName("unnamed." + submission.getLanguage().label);
		}
		Problem problem = problemService.getById(id);
		submission.setProblem(problem);
		submission.setSubmissionStatus(SubmissionStatus.SUBMITTED);
		Submission submissionResult = submissionService.submit(submission);
		return Result.success(submissionResult);
	}


	@GetMapping("submission/{id}")
	public Result getById(@PathVariable Long id) {
		Submission submission = submissionService.getById(id);
		//        SubmissionSimple submissionSimple = new SubmissionSimple();
		//        BeanUtils.copyProperties(submission, submissionSimple);
		return Result.success(submission);
	}

	//    @GetMapping("submission/{id}/code")
	//    public Result<String> getCodeById(@PathVariable Long id){
	//        String submission = submissionService.getCodeById(id);
	//        return Result.success(submission);
	//    }
	//
	@GetMapping("user/{id}/submissions")
	public Result getAllByUserIdPage(@PathVariable Long id,
									 @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
									 @RequestParam(value = "size", defaultValue = "20", required = false) Integer size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Submission> submission = submissionService.getAllSubmissionsByUser(id, pageable);
		return Result.success(submission);
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
}
