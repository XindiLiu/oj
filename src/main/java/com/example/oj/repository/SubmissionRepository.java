package com.example.oj.repository;

import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import com.example.oj.entity.Submission;
import com.example.oj.projection.SubmissionFullDTOProj;
import com.example.oj.DTO.SubmissionSimpleDTO;
import com.example.oj.constant.SubmissionStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SubmissionRepository
		extends JpaRepository<Submission, Long>, PagingAndSortingRepository<Submission, Long> {

	Submission save(Submission submission);

	//    @Query("SELECT s.id,s.language, s.createTime from Submission s where s.id = :id")
	Submission getById(Long id);

	@Query("SELECT new com.example.oj.DTO.SubmissionSimpleDTO(s.id, s.user.id, s.user.displayName, s.problem.id, s.problem.title, s.createTime, s.language, s.status, "
			+
			"s.judgement, s.runTimeMs, s.memoryByte, s.numPassedCases, s.totalCases, s.score) " +
			"FROM Submission s WHERE s.id = :id")
	SubmissionSimpleDTO findProblemSimpleById(Long id);

	SubmissionFullDTOProj findSubmissionInfoById(Long id);

	@Query("SELECT new com.example.oj.DTO.SubmissionSimpleDTO(s.id, s.user.id, s.user.displayName, s.problem.id, s.problem.title, s.createTime, s.language, s.status, "
			+
			"s.judgement, s.runTimeMs, s.memoryByte, s.numPassedCases, s.totalCases, s.score) " +
			"FROM Submission s WHERE s.user.id = :userId ORDER BY s.createTime DESC")
	Page<SubmissionSimpleDTO> findSimpleByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

	//    @Query("SELECT s.code from Submission s where s.id = :id")
	//    String getCodeById(Long id);
	//    @Query("SELECT s.id from Submission s")
	//    Set<Submission> find();
	//
	////    List<Submission> findByUserIdOrderByCreateTimeDesc(Long id);
	//    @Query("SELECT s from Submission s where s.user.id = :id")
	Page<Submission> findByUserIdOrderByCreateTimeDesc(Long id, Pageable pageable);

	Page<Submission> findByProblemIdOrderByCreateTimeDesc(Long id, Pageable pageable);

	//	List<Submission> findByProblemIdAndStatusAndJudgementOrderByRunTimeMs(Long id, SubmissionStatus status, SubmissionResultType judgement, Sort sort, Limit limit);
	@Query("SELECT new com.example.oj.DTO.SubmissionSimpleDTO(s.id, s.user.id, s.user.displayName, s.problem.id, s.problem.title, s.createTime, s.language, s.status, "
			+
			"s.judgement, s.runTimeMs, s.memoryByte, s.numPassedCases, s.totalCases, s.score) " +
			"FROM Submission s WHERE s.problem.id = :id and s.language = :language and s.judgement=:judgement and s.status = :status ORDER BY s.runTimeMs ASC")
	List<SubmissionSimpleDTO> findFastes(Long id,
										 ProgrammingLanguage language, SubmissionStatus status, SubmissionResultType judgement,
										 Pageable pageable);

	Window<SubmissionSimpleDTO> findFirst10ByProblemIdAndLanguageAndStatusAndJudgementOrderByRunTimeMs(Long id,
																									   ProgrammingLanguage language, SubmissionStatus status, SubmissionResultType judgement,
																									   OffsetScrollPosition position);
	//    Submission getByUser(Long id);
	//
	//    Submission getByProblem(Long id);
}
