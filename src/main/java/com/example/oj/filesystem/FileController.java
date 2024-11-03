package com.example.oj.filesystem;

import com.example.oj.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
	@Autowired
	FileService fileService;

	@PostMapping("/upload")
	public Result uploadFile(@RequestParam MultipartFile file) {
		fileService.uploadFile(file, file.getName());
		return Result.success();
	}

	@PostMapping("/upload/zip")
	public Result uploadZip(@RequestParam MultipartFile file) {
		fileService.uploadZip(file, file.getName());
		return Result.success();
	}

	@PostMapping("/type")
	public Result type(@RequestParam MultipartFile file) {

		return Result.success(fileService.type(file));
	}
}
