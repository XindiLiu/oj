package com.example.oj.testcase;

import com.example.oj.filesystem.FileConfig;
import com.example.oj.filesystem.FileService;
import com.example.oj.problem.Problem;
import com.example.oj.problem.ProblemRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TestCaseService {
	@Autowired
	TestCaseRepository testCaseRepository;
	@Autowired
	ProblemRepository problemRepository;
	@Autowired
	FileService fileService;
	@Value("${filesys.testCaseDir}")
	Path testCasesPath;


	@Transactional
	public void save(@RequestBody TestCase testCase) {
		testCaseRepository.save(testCase);
	}

	public List<TestCase> getByProblemId(Long problemId) {
		return testCaseRepository.getByProblemId(problemId);
	}

	public List<TestCase> getTestCasesByProblem(Problem problem) {
		return problem.getTestCases();
	}

	public void delete(Long testCaseId) {
		testCaseRepository.deleteByTestCaseId(testCaseId);
	}


	@Transactional
	public List<TestCase> saveTestCases(MultipartFile zipFile, Long problemId) throws IOException {
		// Validate that the uploaded file is a ZIP
		if (zipFile.isEmpty() || !fileService.isZip(zipFile)) {
			throw new RuntimeException("Uploaded file is not a valid ZIP archive.");
		}
		Path zipPath = fileService.extractZip(zipFile);

		// Match in and out files
		File[] inputs = zipPath.toFile().listFiles((f) -> {
			return f.toString().endsWith(".in") && !f.isDirectory();
		});
		File[] outputs = zipPath.toFile().listFiles((f) -> {
			return f.toString().endsWith(".out") && !f.isDirectory();
		});

		Set<String> inputNames = Arrays.stream(inputs).map((f) -> FilenameUtils.getBaseName(f.getName()))
				.collect(Collectors.toSet());
		Arrays.sort(outputs);
		List<TestCase> testCaseList = new ArrayList<>();

		// Permanent path for the testcase files
		Path testcasePath = testCasesPath.resolve(problemId.toString());
		if (!Files.exists(testcasePath)) {
			fileService.mkDir(testcasePath);
		}

		// Create TestCase objects if there is a match between in and out, add to list.
		for (File output : outputs) {
			String testCaseName = FilenameUtils.getBaseName(output.getName());
			if (inputNames.contains(testCaseName)) {
				TestCase testCase = new TestCase();
				testCase.problem = problemRepository.getById(problemId);
				testCase.setName(testCaseName);

				// Copy valid in/out files to a permanent location
				Path newInputPath = testcasePath.resolve(testCaseName + ".in");
				fileService.copyFile(zipPath.resolve(testCaseName + ".in"), newInputPath);
				Path newOutputPath = testcasePath.resolve(testCaseName + ".out");
				fileService.copyFile(zipPath.resolve(testCaseName + ".out"), newOutputPath);
				testCase.setInputPath(newInputPath.toAbsolutePath().toString());
				testCase.setOutputPath(newOutputPath.toAbsolutePath().toString());

				// USe the temp dir to store files
//				testCase.setInputPath(zipPath.resolve(testCaseName + ".in").toAbsolutePath().toString());
//				testCase.setOutputPath(zipPath.resolve(testCaseName + ".out").toAbsolutePath().toString());

				testCaseList.add(testCase);
			}
		}

		// Update database
		testCaseRepository.deleteByProblemId(problemId);
		testCaseRepository.saveAll(testCaseList);
//		fileService.rmDir(zipPath);
		if (testCaseList.isEmpty()) {
			return null;
		} else {
			return testCaseList;
		}
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
