package com.example.oj.service;

import com.example.oj.dto.ProblemCreateDTO;
import com.example.oj.dto.ProblemSearchDTO;
import com.example.oj.dto.ProblemUserDTO;
import com.example.oj.entity.Problem;
import com.example.oj.entity.ProblemDetail;
import com.example.oj.entity.User;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.dto.ProblemSimpleProj;
import com.example.oj.repository.ProblemDetailRepository;
import com.example.oj.repository.ProblemRepository;
import com.example.oj.utils.SecurityUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

	@Mock
	private ProblemRepository problemRepository;
	@Mock
	private ProblemDetailRepository detailRepository;
	@Mock
	private UserProblemResultService userProblemResultService;
	@Mock
	private EntityManager entityManager;
	@InjectMocks
	private ProblemService problemService;
	private Problem testProblem;
	private ProblemDetail testProblemDetail;
	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setUsername("testUser");

		testProblem = new Problem();
		testProblem.setId(1L);
		testProblem.setTitle("Test Problem");

		testProblemDetail = new ProblemDetail();
		testProblemDetail.setId(1L);
		testProblemDetail.setProblem(testProblem);
		testProblem.setProblemDetail(testProblemDetail);
	}

	@AfterEach
	void resetMocks() {
		clearInvocations(problemRepository);
		reset(detailRepository);
	}

	@Test
	void getById_ShouldReturnProblem() {
		when(problemRepository.getById(1L)).thenReturn(testProblem);

		Problem result = problemService.getById(1L);

		assertNotNull(result);
		assertEquals(testProblem.getId(), result.getId());
		assertEquals(testProblem.getTitle(), result.getTitle());
		verify(problemRepository).getById(1L);
	}

	@Test
	void add_ShouldSaveProblemDetail() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);
			when(detailRepository.save(any(ProblemDetail.class))).thenReturn(testProblemDetail);

			ProblemDetail result = problemService.add(testProblem);

			assertNotNull(result);
			assertEquals(testProblemDetail.getId(), result.getId());
			assertEquals(testUser.getId(), testProblem.getUpdateUser());
			verify(detailRepository).save(testProblemDetail);
		}
	}

	@Test
	void update_ShouldUpdateProblemDetail() throws IdNotFoundException {
		ProblemCreateDTO updateDTO = new ProblemCreateDTO();
		updateDTO.setTitle("Updated Title");

		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);
			when(detailRepository.findById(1L)).thenReturn(Optional.of(testProblemDetail));
			when(detailRepository.save(any(ProblemDetail.class))).thenReturn(testProblemDetail);

			ProblemDetail result = problemService.update(1L, updateDTO);

			assertNotNull(result);
			verify(detailRepository).findById(1L);
			verify(detailRepository).save(any(ProblemDetail.class));
		}
	}

	@Test
	void update_ShouldThrowIdNotFoundException() {
		ProblemCreateDTO updateDTO = new ProblemCreateDTO();
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUser).thenReturn(testUser);
			when(detailRepository.findById(1L)).thenReturn(Optional.empty());
			assertThrows(IdNotFoundException.class, () -> problemService.update(1L, updateDTO));
		}
	}

	@Test
	void getSimpleById_ShouldReturnProblemSimpleProj() {
		// Given
		ProblemSimpleProj expectedProj = mock(ProblemSimpleProj.class);
		when(problemRepository.findProblemSimpleById(1L)).thenReturn(expectedProj);

		// When
		ProblemSimpleProj result = problemService.getSimpleById(1L);

		// Then
		assertNotNull(result);
		assertEquals(expectedProj, result);
		verify(problemRepository).findProblemSimpleById(1L);
	}

	@Test
	void getPagedProblem_ShouldReturnPagedProblemUserDTO() {
		// Given
		ProblemSearchDTO searchDTO = new ProblemSearchDTO();
		int pageNo = 0;
		int pageSize = 10;

		Problem problem1 = new Problem();
		problem1.setId(1L);
		problem1.setTitle("Problem 1");

		Problem problem2 = new Problem();
		problem2.setId(2L);
		problem2.setTitle("Problem 2");

		Page<Problem> problemPage = new PageImpl<>(
				Arrays.asList(problem1, problem2),
				PageRequest.of(pageNo, pageSize),
				2);

		when(problemRepository.findAll(any(Specification.class), any(PageRequest.class)))
				.thenReturn(problemPage);

		// When
		Page<ProblemUserDTO> result = problemService.getPagedProblem(pageNo, pageSize, searchDTO);

		// Then
		assertNotNull(result);
		assertEquals(2, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(2, result.getContent().size());

		// Verify the content is correctly mapped
		assertEquals(problem1.getId(), result.getContent().get(0).getId());
		assertEquals(problem2.getId(), result.getContent().get(1).getId());

		verify(problemRepository).findAll(any(Specification.class), any(PageRequest.class));
	}

	@Test
	void getPagedProblem_ShouldReturnEmptyPage() {
		// Given
		ProblemSearchDTO searchDTO = new ProblemSearchDTO();
		int pageNo = 0;
		int pageSize = 10;

		Page<Problem> emptyPage = new PageImpl<>(
				Arrays.asList(),
				PageRequest.of(pageNo, pageSize),
				0);

		when(problemRepository.findAll(any(Specification.class), any(PageRequest.class)))
				.thenReturn(emptyPage);

		// When
		Page<ProblemUserDTO> result = problemService.getPagedProblem(pageNo, pageSize, searchDTO);

		// Then
		assertNotNull(result);
		assertEquals(0, result.getTotalElements());
		assertEquals(0, result.getTotalPages());
		assertTrue(result.getContent().isEmpty());

		verify(problemRepository).findAll(any(Specification.class), any(PageRequest.class));
	}

	@Test
	void getPagedProblemWithUser_WhenUserIdIsNull_ShouldCallGetPagedProblem() {
		// Given
		ProblemSearchDTO searchDTO = new ProblemSearchDTO();
		searchDTO.setFilterUserId(null);
		int pageNo = 0;
		int pageSize = 10;

		Problem problem = new Problem();
		problem.setId(1L);
		Page<Problem> problemPage = new PageImpl<>(
				Arrays.asList(problem),
				PageRequest.of(pageNo, pageSize),
				1);

		when(problemRepository.findAll(any(Specification.class), any(PageRequest.class)))
				.thenReturn(problemPage);

		// When
		Page<ProblemUserDTO> result = problemService.getPagedProblemWithUser(pageNo, pageSize, searchDTO);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		verify(problemRepository).findAll(any(Specification.class), any(PageRequest.class));
	}

}