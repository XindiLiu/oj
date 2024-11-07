package com.example.oj.problem;

import com.example.oj.constant.DifficultyBoundary;
import com.example.oj.problemDetail.ProblemDetail;
import com.example.oj.problemDetail.ProblemDetailRepository;
import com.example.oj.user.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProblemService {
	@Autowired
	ProblemRepository problemRepository;
	@Autowired
	ProblemDetailRepository detailRepository;

	@Transactional
	public Problem getById(Long id) {
		Problem problem = problemRepository.getById(id);
		return problem;
	}

	public ProblemDetail getDetailById(Long id) {
		ProblemDetail detail = detailRepository.getById(id);
		return detail;
	}

	@Transactional
	public void save(Problem problem) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		problem.setCreateUser(user.getId());
		problem.setUpdateUser(user.getId());
		problemRepository.save(problem);
	}

	@Transactional
	public void update(Problem problem) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		problem.setUpdateUser(user.getId());
		problemRepository.save(problem);
	}

//	@Transactional
//	public void save(ProblemDetail problemDetail) {
//		detailRepository.save(problemDetail);
////        Problem problem = new Problem();
////        problem.setId(problemDetail.getId());
////        problemRepository.save(problem);
//	}

	public Page<Problem> page(int pageNo, int pageSize, ProblemPageDTO problemPageDTO) {

		// 构造自定义查询条件
		Specification<Problem> queryCondition = new Specification<Problem>() {
			@Override
			public Predicate toPredicate(Root<Problem> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();

				if (problemPageDTO.createUser != null) {
					predicateList.add(criteriaBuilder.equal(root.get("createUser"), problemPageDTO.createUser));
				}
				int difficultyUpperBound = problemPageDTO.difficultyUpperBound != null ? problemPageDTO.difficultyUpperBound : DifficultyBoundary.max;
				int difficultyLowerBound = problemPageDTO.difficultyLowerBound != null ? problemPageDTO.difficultyLowerBound : DifficultyBoundary.min;

				predicateList.add(criteriaBuilder.between(root.get("difficulty"), difficultyLowerBound, difficultyUpperBound));
				return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
			}
		};

		// 分页和不分页，这里按起始页和每页展示条数为0时默认为不分页，分页的话按创建时间降序
		try {
			var result = problemRepository.findAll(queryCondition,
					PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime")));
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void uploadTestData(MultipartFile file) {
	}

	public ProblemSimplePorj getSimpleById(Long id) {

		return problemRepository.findProblemSimpleById(id);
	}
}
