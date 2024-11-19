package com.example.oj.submission;

import com.example.oj.constant.ProgrammingLanguage;
import com.example.oj.constant.SubmissionResultType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubmissionRepository
		extends JpaRepository<Submission, Long>, PagingAndSortingRepository<Submission, Long> {

	Submission save(Submission submission);

	//    @Query("SELECT s.id,s.language, s.createTime from Submission s where s.id = :id")
	Submission getById(Long id);

	@Query("SELECT new com.example.oj.submission.SubmissionSimple(s.id, s.user.id, s.problem.id, s.createTime, s.language, s.status, "
			+
			"s.judgement, s.runTimeMs, s.memoryByte, s.numPassedCases, s.totalCases, s.score) " +
			"FROM Submission s WHERE s.id = :id")
	SubmissionSimple findProblemSimpleById(Long id);

	@Query("SELECT new com.example.oj.submission.SubmissionSimple(s.id, s.user.id, s.problem.id, s.createTime, s.language, s.status, "
			+
			"s.judgement, s.runTimeMs, s.memoryByte, s.numPassedCases, s.totalCases, s.score) " +
			"FROM Submission s WHERE s.user.id = :userId ORDER BY s.createTime DESC")
	Page<SubmissionSimple> findSimpleByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

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

	Window<Submission> findFirst10ByProblemIdAndLanguageAndStatusAndJudgementOrderByRunTimeMs(Long id,
																							  ProgrammingLanguage language, SubmissionStatus status, SubmissionResultType judgement,
																							  OffsetScrollPosition position);

	//    Submission getByUser(Long id);
	//
	//    Submission getByProblem(Long id);
}
