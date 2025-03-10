package com.example.oj;

import com.example.oj.filesystem.FileService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;

// TODO：Junit mockito wiremock spring test
// TODO: Unit test 和 integration test
@SpringBootTest
class OjApplicationTests {

	@Autowired
	FileService fileService;

	@Test
	void mian() throws IOException {
		Path file = Paths.get("data/dat.zip");
		System.out.println(Files.probeContentType(file));
	}

	@Test
	void submit() {

	}

}
