package com.example.oj.repository;

import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.entity.Submission;
import com.example.oj.dto.SubmissionFullDTOProj;
import com.example.oj.dto.SubmissionSimpleDTO;
import com.example.oj.constant.SubmissionStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SubmissionRepository
		extends JpaRepository<Submission, Long>, PagingAndSortingRepository<Submission, Long> {

	Submission save(Submission submission);

	Submission getById(Long id);

	@Query("SELECT new com.example.oj.dto.SubmissionSimpleDTO(s.id, s.user.id, s.user.displayName, s.problem.id, s.problem.title, s.createTime, s.language, s.status, "
			+
			"s.judgement, s.runTimeMs, s.memoryByte, s.numPassedCases, s.totalCases, s.score) " +
			"FROM Submission s WHERE s.id = :id")
	SubmissionSimpleDTO findProblemSimpleById(Long id);

	SubmissionFullDTOProj findSubmissionInfoById(Long id);

	@Query("SELECT new com.example.oj.dto.SubmissionSimpleDTO(s.id, s.user.id, s.user.displayName, s.problem.id, s.problem.title, s.createTime, s.language, s.status, "
			+
			"s.judgement, s.runTimeMs, s.memoryByte, s.numPassedCases, s.totalCases, s.score) " +
			"FROM Submission s WHERE s.user.id = :userId ORDER BY s.createTime DESC")
	Page<SubmissionSimpleDTO> findSimpleByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);


	Page<Submission> findByProblemIdOrderByCreateTimeDesc(Long id, Pageable pageable);

	@Query("SELECT new com.example.oj.dto.SubmissionSimpleDTO(s.id, s.user.id, s.user.displayName, s.problem.id, s.problem.title, s.createTime, s.language, s.status, "
			+
			"s.judgement, s.runTimeMs, s.memoryByte, s.numPassedCases, s.totalCases, s.score) " +
			"FROM Submission s WHERE s.problem.id = :id and s.language = :language and s.judgement=:judgement and s.status = :status ORDER BY s.runTimeMs ASC")
	List<SubmissionSimpleDTO> findFastes(Long id,
										 ProgrammingLanguage language, SubmissionStatus status, SubmissionResultType judgement,
										 Pageable pageable);

}
