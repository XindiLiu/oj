package com.example.oj.submission;

import com.example.oj.annotation.AutoFill;
import com.example.oj.submission.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

public interface SubmissionRepository extends JpaRepository<Submission, Long>, PagingAndSortingRepository<Submission, Long>{

    Submission save(Submission submission);
//    @Query("SELECT s.id,s.language, s.createTime from Submission s where s.id = :id")
    Submission getById(Long id);
//    @Query("SELECT s.code from Submission s where s.id = :id")
//    String getCodeById(Long id);
//    @Query("SELECT s.id from Submission s")
//    Set<Submission> find();
//
////    List<Submission> findByUserIdOrderByCreateTimeDesc(Long id);
//    @Query("SELECT s from Submission s where s.user.id = :id")
    Page<Submission> findByUserIdOrderByCreateTimeDesc(Long id, Pageable pageable);
    Page<Submission> findByProblemIdOrderByCreateTimeDesc(Long id, Pageable pageable);
//    Submission getByUser(Long id);
//
//    Submission getByProblem(Long id);
}
