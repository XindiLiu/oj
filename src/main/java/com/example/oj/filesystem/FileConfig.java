package com.example.oj.filesystem;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@ConfigurationProperties(prefix = "filesys")
@Data
public class FileConfig {
    String rootDir;
    String tempDir;

}
