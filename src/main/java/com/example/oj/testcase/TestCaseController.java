package com.example.oj.testcase;

import com.example.oj.common.Result;
import com.example.oj.problem.Problem;
import com.example.oj.problem.ProblemRepository;
import com.example.oj.problem.ProblemServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class TestCaseController{
    @Autowired
    TestCaseServiceImpl testCaseService;
    @Autowired
    ProblemServiceImpl problemRService;

    @PostMapping("/problem/testCase")
    @ResponseBody
    public Result save(@RequestBody TestCaseDTO testCaseDTO){
//    public Result save(@RequestBody TestCase testCase){
        TestCase testCase = new TestCase();
        BeanUtils.copyProperties(testCaseDTO, testCase);
        Problem problem = problemRService.getById(testCaseDTO.getProblemId());
        testCase.setProblem(problem);
        testCaseService.save(testCase);
        return Result.success();
    }

    @GetMapping("/problem/{problemId}/testcases")
    @ResponseBody
    public Result<List<TestCase>> getByProblemId(@PathVariable Long problemId){
//    public Result save(@RequestBody TestCase testCase){
        List<TestCase> testCases = testCaseService.getByProblemId(problemId);
        return Result.success(testCases);
    }
    @DeleteMapping("/testcase/{id}")
    @ResponseBody
    public Result delete(@PathVariable Long testCaseId){
        testCaseService.delete(testCaseId);
        return Result.success();
    }

}
