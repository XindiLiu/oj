package com.example.oj.testcase;

import com.example.oj.common.Result;
import com.example.oj.filesystem.FileServiceImpl;
import com.example.oj.problem.Problem;
import com.example.oj.problem.ProblemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class TestCaseServiceImpl {
    @Autowired
    TestCaseRepository testCaseRepository;
    @Autowired
    ProblemRepository problemRepository;

    @Transactional
    public void save(@RequestBody TestCase testCase){
        testCaseRepository.save(testCase);
    }

    public List<TestCase> getByProblemId(Long problemId) {
        return testCaseRepository.getByProblemId(problemId);
    }

    public void delete(Long testCaseId) {
        testCaseRepository.deleteByTestCaseId(testCaseId);
    }

//    @Transactional
//    public void uploadTestCases(MultipartFile file, Long id)  {
//        Path tempDirPath = fileService.mkTempDir(file.getName());
//        if (!fileService.isZip(file)){
//            throw new RuntimeException("Not zip file");
//        }
//        Path tempFilePath;
//        try {
//            tempFilePath = Paths.get(tempDirPath.toString(), file.getName());
//            file.transferTo(tempFilePath);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
////            e.printStackTrace();
//        }
//        log.info("Temp zip file saved in: {}", tempDirPath);
//
//        File unzipDestinationDir = new File(tempDirPath.toString(), file.getName()+".unzip");
//        unzipDestinationDir.mkdir();
//        try {
//            fileService.getValidTestCases(tempFilePath);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }
}
