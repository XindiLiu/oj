package com.example.oj;

import com.example.oj.dto.MessageDTO;
import com.example.oj.rabbit.MessageSender;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class OjApplicationTests {
    @Autowired
    MessageSender sender;
    @Test
    void mian() throws IOException {
        Path file = Paths.get("data/dat.zip");
        System.out.println(Files.probeContentType(file));
    }

}
