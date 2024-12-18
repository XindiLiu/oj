package com.example.oj.problem;

import com.example.oj.common.Result;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.problemDetail.ProblemDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/problem")
@Slf4j
public class ProblemController {
	@Autowired
	ProblemService problemService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/add")
	public Result add(@RequestBody ProblemCreateDTO problemCreateDTO) {
		Problem problem = Problem.emptyProblem();
		if (problemCreateDTO.getSampleIo() != null) {
			var sampleList = problemCreateDTO.getSampleIo();
			for (int i = 0; i < sampleList.size(); i++) {
				sampleList.get(i).setSampleId(i + 1);
			}
		}
		BeanUtils.copyProperties(problemCreateDTO, problem);
		BeanUtils.copyProperties(problemCreateDTO, problem.getProblemDetail());
		ProblemDetail problemDetail = problemService.add(problem);
		return Result.success(problemDetail);
	}

	// TODO: Only allow updating problems created by the current user.
	@PostMapping("/update/{id}")
	public Result update(@PathVariable Long id, @RequestBody ProblemCreateDTO problemCreateDTO)
			throws IdNotFoundException {
		// Manually assign a number to the sample input and output
		if (problemCreateDTO.getSampleIo() != null) {
			var sampleList = problemCreateDTO.getSampleIo();
			for (int i = 0; i < sampleList.size(); i++) {
				sampleList.get(i).setSampleId(i + 1);
			}
		}
		ProblemDetail problemDetail = problemService.update(id, problemCreateDTO);
		return Result.success(problemDetail);
	}

//	@GetMapping("/{id}")
//	public Result getById(@PathVariable Long id) {
//		Problem problem = problemService.getById(id);
//		problem.setProblemDetail(null);
//		//        problem.getProblemDetail();
//		return Result.success(problem);
//	}

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
