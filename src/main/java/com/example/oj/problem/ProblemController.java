package com.example.oj.problem;

import com.example.oj.common.Result;
import com.example.oj.problemDetail.ProblemDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/problem")
@Slf4j
public class ProblemController {
    @Autowired
    ProblemServiceImpl problemService;

    @PostMapping
    public Result save(@RequestBody ProblemDTO problemDTO){
        Problem problem = new Problem();
        ProblemDetail detail = new ProblemDetail();
        BeanUtils.copyProperties(problemDTO, problem);
        BeanUtils.copyProperties(problemDTO, detail);
        problem.setProblemDetail(detail);
        detail.setProblem(problem);
        problemService.save(problem);
        return Result.success();
    }

    @PostMapping("data")
    public Result uploadTestData(@RequestParam MultipartFile file){
        problemService.uploadTestData(file);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody Problem problem){
        problemService.save(problem);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){
        Problem problem = problemService.getById(id);
        problem.setProblemDetail(null);
//        problem.getProblemDetail();
        return Result.success(problem);
    }

    @GetMapping("/detail/{id}")
    public Result getDetailById(@PathVariable Long id){
        ProblemDetail problem = problemService.getDetailById(id);
//        problem.getProblemDetail();
        return Result.success(problem);
    }

    @GetMapping("/page")
    public Result page(@RequestParam int pageNumber, @RequestParam int pageSize, @RequestBody ProblemPageDTO problemPageDTO){
        Page<Problem> problemPage = problemService.page(pageNumber, pageSize, problemPageDTO);
        return Result.success(problemPage);
    }

}
