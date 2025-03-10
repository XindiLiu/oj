package com.example.oj.service;

import com.example.oj.constant.Result;
import com.example.oj.constant.Role;
import com.example.oj.dto.UserLoginDTO;
import com.example.oj.dto.UserUpdateDTO;
import com.example.oj.entity.User;
import com.example.oj.exception.AlreadyLoggedInException;
import com.example.oj.exception.IdNotFoundException;
import com.example.oj.repository.UserRepository;
import com.example.oj.utils.JwtUtil;
import com.example.oj.utils.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private UserService userService;

	private User testUser;
	private UserLoginDTO loginDTO;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setUsername("testUser");
		testUser.setPassword("password");
		testUser.setRole(Role.USER);
		testUser.setScore(0L);

		loginDTO = new UserLoginDTO();
		loginDTO.setUsername("testUser");
		loginDTO.setPassword("password");
	}

	@Test
	void login_ShouldReturnJWT() throws AlreadyLoggedInException {
		// Setup
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::isGuest).thenReturn(true);
			when(userRepository.findByUsername("testUser")).thenReturn(testUser);
			when(jwtUtil.generateJWT(any())).thenReturn("testJWT");

			// Execute
			String jwt = userService.login(loginDTO);

			// Verify
			assertEquals("testJWT", jwt);
			verify(authenticationManager).authenticate(any());
			verify(userRepository).findByUsername("testUser");
			verify(jwtUtil).generateJWT(testUser);
		}
	}

	@Test
	void login_ShouldThrowAlreadyLoggedInException() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::isGuest).thenReturn(false);

			assertThrows(AlreadyLoggedInException.class, () -> userService.login(loginDTO));
		}
	}

	@Test
	void register_ShouldCreateNewUser() {
		// Setup
		when(userRepository.existsByUsername("testUser")).thenReturn(false);
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRepository.save(any())).thenReturn(testUser);

		// Execute
		User result = userService.register(testUser);

		// Verify
		assertNotNull(result);
		verify(userRepository).existsByUsername("testUser");
		verify(passwordEncoder).encode("password");
		verify(userRepository).save(any());
	}

	@Test
	void register_ShouldReturnNullWhenUsernameExists() {
		when(userRepository.existsByUsername("testUser")).thenReturn(true);

		User result = userService.register(testUser);

		assertNull(result);
		verify(userRepository).existsByUsername("testUser");
		verify(userRepository, never()).save(any());
	}

	@Test
	void updatePassword_ShouldSucceed() throws IdNotFoundException {
		// Setup
		when(userRepository.getUserById(1L)).thenReturn(testUser);
		when(passwordEncoder.matches("oldPassword", testUser.getPassword())).thenReturn(true);
		when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
		when(userRepository.updatePasswordById(eq(1L), any())).thenReturn(1);

		// Execute
		Result result = userService.updatePassword(1L, "oldPassword", "newPassword");

		// Verify
		assertTrue(result.isSuccess());
		verify(userRepository).updatePasswordById(eq(1L), any());
	}

	@Test
	void updatePassword_ShouldFailWithWrongOldPassword() throws IdNotFoundException {
		// Setup
		when(userRepository.getUserById(1L)).thenReturn(testUser);
		when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

		// Execute
		Result result = userService.updatePassword(1L, "wrongPassword", "newPassword");

		// Verify
		assertFalse(result.isSuccess());
		assertEquals(1, result.getCode());
		verify(userRepository, never()).updatePasswordById(any(), any());
	}

	@Test
	void update_ShouldUpdateUser() throws IdNotFoundException {
		// Setup
		UserUpdateDTO updateDTO = new UserUpdateDTO();
		updateDTO.setId(1L);
		updateDTO.setDisplayName("newUsername");

		when(userRepository.getUserById(1L)).thenReturn(testUser);
		when(userRepository.save(any())).thenReturn(testUser);

		// Execute
		User result = userService.update(updateDTO);

		// Verify
		assertNotNull(result);
		verify(userRepository).getUserById(1L);
		verify(userRepository).save(any());
	}

	@Test
	void update_ShouldThrowIdNotFoundException() {
		UserUpdateDTO updateDTO = new UserUpdateDTO();
		updateDTO.setId(1L);

		when(userRepository.getUserById(1L)).thenReturn(null);

		assertThrows(IdNotFoundException.class, () -> userService.update(updateDTO));
	}

	@Test
	void getById_ShouldReturnUser() {
		when(userRepository.getUserById(1L)).thenReturn(testUser);

		User result = userService.getById(1L);

		assertNotNull(result);
		assertEquals(testUser, result);
		verify(userRepository).getUserById(1L);
	}
}