package com.example.oj.service;

import com.example.oj.constant.SubmissionResultType;
import com.example.oj.entity.*;
import com.example.oj.repository.UserProblemResultRepository;
import com.example.oj.repository.UserRepository;
import com.example.oj.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProblemResultServiceTest {

	@Mock
	private UserProblemResultRepository userProblemResultRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserProblemResultService userProblemResultService;

	private User testUser;
	private Problem testProblem;
	private Submission testSubmission;
	private UserProblemResult testUserProblemResult;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);

		testProblem = new Problem();
		testProblem.setId(1L);

		testSubmission = new Submission();
		testSubmission.setId(1L);
		testSubmission.setUser(testUser);
		testSubmission.setProblem(testProblem);
		testSubmission.setScore(100);
		testSubmission.setJudgement(SubmissionResultType.AC);

		testUserProblemResult = new UserProblemResult(testUser, testProblem, testSubmission);
	}

	@Test
	void afterCodeTesting_ShouldCreateNewResult() {
		when(userProblemResultRepository.findById(any())).thenReturn(Optional.empty());
		when(userProblemResultRepository.save(any())).thenReturn(testUserProblemResult);

		userProblemResultService.afterCodeTesting(testSubmission);

		verify(userProblemResultRepository).save(any());
		verify(userRepository).incrementScore(eq(1L), eq(100L));
	}

	@Test
	void afterCodeTesting_ShouldUpdateExistingResult() {
		UserProblemResult existingResult = new UserProblemResult(testUser, testProblem, testSubmission);
		existingResult.setHighestScore(50);

		when(userProblemResultRepository.findById(any())).thenReturn(Optional.of(existingResult));
		when(userProblemResultRepository.save(any())).thenReturn(testUserProblemResult);

		userProblemResultService.afterCodeTesting(testSubmission);

		verify(userProblemResultRepository).save(any());
		verify(userRepository).incrementScore(eq(1L), eq(50L));
	}

	@Test
	void getUserProblem_ShouldReturnResult() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);
			when(userProblemResultRepository.findById_UserIdAndId_ProblemId(1L, 1L))
					.thenReturn(Optional.of(testUserProblemResult));

			UserProblemResult result = userProblemResultService.getUserProblem(1L);

			assertNotNull(result);
			assertEquals(testUserProblemResult, result);
		}
	}

	@Test
	void getUserProblem_ShouldReturnNull_WhenNotFound() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);
			when(userProblemResultRepository.findById_UserIdAndId_ProblemId(1L, 1L))
					.thenReturn(Optional.empty());

			UserProblemResult result = userProblemResultService.getUserProblem(1L);

			assertNull(result);
		}
	}
    
}