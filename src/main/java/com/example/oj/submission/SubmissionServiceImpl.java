package com.example.oj.submission;

import com.example.oj.annotation.AutoFill;
import com.example.oj.submission.Submission;
import com.example.oj.submission.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SubmissionServiceImpl {
    @Autowired
    SubmissionRepository submissionRepository;
    @AutoFill
    public Long submit(Submission submission) {
        Submission savedSubmission = submissionRepository.save(submission);
        return savedSubmission.getId();
    }

    public Submission getById(Long id) {
        Submission submission = submissionRepository.getById(id);
        return submission;
    }
//    public String getCodeById(Long id) {
//        String submission = submissionRepository.getCodeById(id);
//        return submission;
//    }
//
    public Page<Submission> getAllSubmissionsByUser(Long id, Pageable pageable) {
        Page<Submission> submissions = submissionRepository.findByUserIdOrderByCreateTimeDesc(id, pageable);
        return submissions;
    }
//
//    public Set<Submission> getAll() {
//        Set<Submission> submissions = submissionRepository.find();
//        return submissions;
//    }

    public Page<Submission> getByProblem(Long id, Pageable pageable) {
        Page<Submission> submission = submissionRepository.findByProblemIdOrderByCreateTimeDesc(id, pageable);
        return submission;
    }
}
