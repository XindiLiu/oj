package com.example.oj.service;

import com.example.oj.codetester.CodeTester;
import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionStatus;
import com.example.oj.entity.Problem;
import com.example.oj.entity.Submission;
import com.example.oj.entity.TestCase;
import com.example.oj.entity.User;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.repository.ProblemRepository;
import com.example.oj.repository.SubmissionRepository;
import com.example.oj.repository.UserRepository;
import com.example.oj.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

	@Mock
	private SubmissionRepository submissionRepository;

	@Mock
	private ProblemRepository problemRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CodeTester codeTester;

	@Mock
	private UserProblemResultService userProblemResultService;

	@InjectMocks
	private SubmissionService submissionService;

	private Submission testSubmission;
	private Problem testProblem;
	private User testUser;
	private TestCase testCase;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);

		testProblem = new Problem();
		testProblem.setId(1L);

		testCase = new TestCase();
		testCase.setWeight(100);
		testProblem.setTestCases(Arrays.asList(testCase));

		testSubmission = new Submission();
		testSubmission.setId(1L);
		testSubmission.setProblem(testProblem);
		testSubmission.setUser(testUser);
		testSubmission.setLanguage(ProgrammingLanguage.JAVA);
	}

	@Test
	void submit_ShouldCreateNewSubmission() throws IdNotFoundException {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);
			when(problemRepository.findById(1L)).thenReturn(Optional.of(testProblem));
			when(submissionRepository.save(any(Submission.class))).thenReturn(testSubmission);

			Submission result = submissionService.submit(testSubmission, 1L);

			assertNotNull(result);
			assertEquals(SubmissionStatus.RUNNING, result.getStatus());
			verify(submissionRepository).save(any(Submission.class));
			verify(codeTester).test(eq(testProblem), eq(testSubmission), any());
		}
	}

	@Test
	void afterCodeTesting_ShouldCalculateScore() {
		testSubmission.setStatus(SubmissionStatus.FINISHED);
		testSubmission.setNumPassedCases(1);

		submissionService.afterCodeTesting(testSubmission);

		assertEquals(100, testSubmission.getScore());
		verify(submissionRepository).save(testSubmission);
		verify(userProblemResultService).afterCodeTesting(testSubmission);
	}

	@Test
	void submit_ShouldThrowIdNotFoundException() {
		when(problemRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IdNotFoundException.class, () -> submissionService.submit(testSubmission, 1L));
	}
}