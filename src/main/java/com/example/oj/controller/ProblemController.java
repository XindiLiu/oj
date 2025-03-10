package com.example.oj.controller;

import com.example.oj.constant.Result;
import com.example.oj.entity.Problem;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.dto.ProblemCreateDTO;
import com.example.oj.dto.ProblemSearchDTO;
import com.example.oj.service.ProblemService;
import com.example.oj.dto.ProblemSimpleProj;
import com.example.oj.entity.ProblemDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// TODO: 在controller里用swagger
@RestController
@RequestMapping("/problem")
@Slf4j
@RequiredArgsConstructor
public class ProblemController {

	private final ProblemService problemService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/add")
	public Result add(@RequestBody ProblemCreateDTO problemCreateDTO) {
		Problem problem = Problem.emptyProblem();
		BeanUtils.copyProperties(problemCreateDTO, problem);
		BeanUtils.copyProperties(problemCreateDTO, problem.getProblemDetail());
		ProblemDetail problemDetail = problemService.add(problem);
		return Result.success(problemDetail);
	}

	// TODO: Only allow updating problems created by the current user.
	@PostMapping("/update/{id}")
	public Result update(@PathVariable Long id, @RequestBody ProblemCreateDTO problemCreateDTO)
			throws IdNotFoundException {
		ProblemDetail problemDetail = problemService.update(id, problemCreateDTO);
		return Result.success(problemDetail);
	}

	/*
	 * Full description of a problem.
	 */
	@GetMapping("/detail/{id}")
	public Result getDetailById(@PathVariable Long id) {
		ProblemDetail problem = problemService.getDetailById(id);
		//        problem.getProblemDetail();
		return Result.success(problem);
	}

	@GetMapping("/simple/{id}")
	public Result getSimpleById(@PathVariable Long id) {
		ProblemSimpleProj problem = problemService.getSimpleById(id);
		return Result.success(problem);
	}

	/*
	 * Get paged problems with UserProblemResult.
	 */
	@GetMapping("/page")
	public Result getPagedProblemUserDTOs(@RequestParam int pageNumber, @RequestParam int pageSize,
										  ProblemSearchDTO problemSearchDTO) {
		// Fetch the paged data
		var problemUserDTOPage = problemService.getPagedProblemWithUser(pageNumber, pageSize, problemSearchDTO);
		return Result.success(problemUserDTOPage);
	}
}
