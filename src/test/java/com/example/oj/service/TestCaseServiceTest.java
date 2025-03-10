package com.example.oj.service;

import com.example.oj.entity.Problem;
import com.example.oj.entity.TestCase;
import com.example.oj.exception.FileTypeException;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.filesystem.FileService;
import com.example.oj.repository.ProblemRepository;
import com.example.oj.repository.TestCaseRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestCaseServiceTest {

	@Mock
	private TestCaseRepository testCaseRepository;

	@Mock
	private ProblemRepository problemRepository;

	@Mock
	private FileService fileService;

	@InjectMocks
	private TestCaseService testCaseService;

	private Problem testProblem;
	static private Path testCaseDir;
	private TestCase testCase1;
	private TestCase testCase2;

	@BeforeAll
	static void setupTestDirectory() throws IOException {
		testCaseDir = Paths.get("src", "test", "resources", "TestCaseServiceTest");
		if (!Files.exists(testCaseDir)) {
			Files.createDirectories(testCaseDir);
		}
	}

	@AfterEach
	void cleanupTestDirectory() throws IOException {
		if (Files.exists(testCaseDir)) {
			Files.walk(testCaseDir).sorted(Comparator.reverseOrder()).forEach(path -> {
				try {
					if (!path.equals(testCaseDir)) {
						Files.delete(path);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}


	@BeforeEach
	void setUp() {
		testProblem = new Problem();
		testProblem.setId(1L);

		testCase1 = new TestCase();
		testCase1.setTestCaseId(1);
		testCase1.setProblem(testProblem);
		testCase1.setName("test1");
		testCase1.setInput("input1");
		testCase1.setOutput("output1");

		testCase2 = new TestCase();
		testCase2.setTestCaseId(2);
		testCase2.setProblem(testProblem);
		testCase2.setName("test2");
		testCase2.setInput("input2");
		testCase2.setOutput("output2");
	}

	@Test
	void getByProblemId_ReturnTestCases() {
		ArrayList<TestCase> testCases = new ArrayList<>(Arrays.asList(testCase1));
		when(testCaseRepository.getByProblemIdOrderByName(1L)).thenReturn(testCases);

		ArrayList<TestCase> result = testCaseService.getByProblemId(1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(testCase1.getName(), result.get(0).getName());
		verify(testCaseRepository).getByProblemIdOrderByName(1L);
	}

	@Test
	void getByProblemIdWithInOut_ReturnTestCasesWithInputOutput() {
		ArrayList<TestCase> testCases = new ArrayList<>(Arrays.asList(testCase1));
		when(testCaseRepository.getByProblemIdOrderByName(1L)).thenReturn(testCases);

		ArrayList<TestCase> result = testCaseService.getByProblemIdWithInOut(1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("input1", result.get(0).getInput());
		assertEquals("output1", result.get(0).getOutput());
		verify(testCaseRepository).getByProblemIdOrderByName(1L);
	}

	@Test
	void saveTestCases_IgnoreOtherFiles() throws IOException, IdNotFoundException, FileTypeException {
		// Input and output file
		Files.writeString(testCaseDir.resolve(String.format("%s.in", testCase1.getName())), testCase1.getInput());
		Files.writeString(testCaseDir.resolve(String.format("%s.out", testCase1.getName())), testCase1.getOutput());

		// irrelevant file
		Files.writeString(testCaseDir.resolve("not testcase.txt"), "Not input or output");

		MultipartFile zipFile = new MockMultipartFile(
				"testcases.zip",
				"testcases.zip",
				"application/zip",
				"mock zip content".getBytes());

		// Setup mocks
		when(problemRepository.getById(1L)).thenReturn(testProblem);
		when(fileService.isZip(any())).thenReturn(true);
		when(fileService.extractZip(any())).thenReturn(testCaseDir);
		when(testCaseRepository.saveAll(any())).thenReturn(Arrays.asList(testCase1));
		List<TestCase> result = testCaseService.saveTestCases(zipFile, 1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("input1", result.get(0).getInput());
		assertEquals("output1", result.get(0).getOutput());
	}

	@Test
	void saveTestCases_IgnoreDirectories() throws IOException, IdNotFoundException, FileTypeException {
		// Input and output file
		Files.writeString(testCaseDir.resolve(String.format("%s.in", testCase1.getName())), testCase1.getInput());
		Files.writeString(testCaseDir.resolve(String.format("%s.out", testCase1.getName())), testCase1.getOutput());

		// Nested directory
		Path testCaseDir2 = testCaseDir.resolve("testcase2");
		Files.createDirectory(testCaseDir2);
		Files.writeString(testCaseDir2.resolve(String.format("%s.in", testCase2.getName())), testCase2.getInput());
		Files.writeString(testCaseDir2.resolve(String.format("%s.out", testCase2.getName())), testCase2.getOutput());

		MultipartFile zipFile = new MockMultipartFile(
				"testcases.zip",
				"testcases.zip",
				"application/zip",
				"mock zip content".getBytes());

		// Setup mocks
		when(problemRepository.getById(1L)).thenReturn(testProblem);
		when(fileService.isZip(any())).thenReturn(true);
		when(fileService.extractZip(any())).thenReturn(testCaseDir);
		when(testCaseRepository.saveAll(any())).thenReturn(Arrays.asList(testCase1));
		List<TestCase> result = testCaseService.saveTestCases(zipFile, 1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("input1", result.get(0).getInput());
		assertEquals("output1", result.get(0).getOutput());
	}

	@Test
	void saveTestCases_SaveValidTestCases() throws IOException, IdNotFoundException, FileTypeException {
		TestCase[] exampleTestCases = {testCase1, testCase2};

		// Input and output files
		for (TestCase testCase : exampleTestCases) {
			Files.writeString(testCaseDir.resolve(String.format("%s.in", testCase.getName())), testCase.getInput());
			Files.writeString(testCaseDir.resolve(String.format("%s.out", testCase.getName())), testCase.getOutput());
		}

		// Mock MultipartFile
		MultipartFile zipFile = new MockMultipartFile(
				"testcases.zip",
				"testcases.zip",
				"application/zip",
				"mock zip content".getBytes());

		// Setup mocks
		when(problemRepository.getById(1L)).thenReturn(testProblem);
		when(fileService.isZip(any())).thenReturn(true);
		when(fileService.extractZip(any())).thenReturn(testCaseDir);
		when(testCaseRepository.saveAll(any())).thenReturn(Arrays.asList(exampleTestCases));

		// Execute
		List<TestCase> result = testCaseService.saveTestCases(zipFile, 1L);

		// Verify
		assertNotNull(result); // Result not null
		assertEquals(exampleTestCases.length, result.size()); // Two testcases in the result
		verify(testCaseRepository).deleteByProblemId(1L); // Previous saved testcases are cleared
		verify(testCaseRepository).saveAll(argThat(testCases -> {
			List<TestCase> cases = (List<TestCase>) testCases;
			if (cases.size() != exampleTestCases.length)
				return false;

			boolean verifyResult = true;

			for (int i = 0; i < cases.size(); i++) {
				verifyResult = verifyResult && cases.get(i).getName().equals(exampleTestCases[i].getName())
						&& cases.get(i).getInput().equals(exampleTestCases[i].getInput())
						&& cases.get(i).getOutput().equals(exampleTestCases[i].getOutput());
			}
			return verifyResult;
		}));
	}

	@Test
	void saveTestCases_NoTestCaseReturnNull() throws IOException, IdNotFoundException, FileTypeException {
		// irrelevant file
		Files.writeString(testCaseDir.resolve("not testcase.txt"), "Not input or output");

		MultipartFile zipFile = new MockMultipartFile(
				"testcases.zip",
				"testcases.zip",
				"application/zip",
				"mock zip content".getBytes());

		// Setup mocks
		when(problemRepository.getById(1L)).thenReturn(testProblem);
		when(fileService.isZip(any())).thenReturn(true);
		when(fileService.extractZip(any())).thenReturn(testCaseDir);
		List<TestCase> result = testCaseService.saveTestCases(zipFile, 1L);

		assertNull(result);
	}

	@Test
	void saveTestCases_ThrowIdNotFoundException() {
		MultipartFile zipFile = new MockMultipartFile(
				"test.zip", "test.zip",
				"application/zip", "test data".getBytes());

		when(problemRepository.getById(1L)).thenReturn(null);
		assertThrows(IdNotFoundException.class, () -> testCaseService.saveTestCases(zipFile, 1L));
	}

	@Test
	void saveTestCases_ThrowFileTypeException() {
		MultipartFile zipFile = new MockMultipartFile(
				"test.txt", "test.txt",
				"text/plain", "test data".getBytes());

		when(problemRepository.getById(1L)).thenReturn(testProblem);
		when(fileService.isZip(any())).thenReturn(false);

		assertThrows(FileTypeException.class, () -> testCaseService.saveTestCases(zipFile, 1L));
	}

	@Test
	void delete_DeleteTestCase() {
		testCaseService.delete(1L);
		verify(testCaseRepository).deleteByTestCaseId(1L);
	}
}