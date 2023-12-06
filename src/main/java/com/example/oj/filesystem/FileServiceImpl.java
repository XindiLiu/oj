package com.example.oj.filesystem;

import com.example.oj.exception.InvalidTestCaseFileException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
@Slf4j
public class FileServiceImpl {
    @Autowired
    FileConfig fileConfig;
    @Autowired
    Tika tika;
    public final static String inputFileExtension = "in";
    public final static String outputFileExtension = "out";
//    Path tempDirPath = Paths.get(fileConfig.tempDir);
    public void uploadFile(MultipartFile file, String fileName) {
        File dir = new File(fileConfig.rootDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir.getAbsolutePath() + File.separator +  fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadZip(MultipartFile file, String fileName) {
        File dir = new File(fileConfig.rootDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir.getAbsolutePath() + File.separator +  fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Path mkTempDir(String name){
        Path tempDirPath = Paths.get(fileConfig.rootDir);

        try {
            Path tempDir = Files.createTempDirectory(tempDirPath,name);
            return tempDir;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isZip(Path path){
        String fileType = null;
        try {
            fileType = tika.detect(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileType.equals("application/zip");
    }

    public boolean isZip(MultipartFile file) {
        String fileType = null;
        try {
            fileType = tika.detect(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileType.equals("application/zip");
    }

    public String type(MultipartFile file) {
        File dir = new File("data");

        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir = new File(dir.getAbsolutePath() + File.separator +  file.getName());
        try {
            file.transferTo(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String mimeType = null;
        try {
            mimeType = tika.detect(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mimeType;
    }

    /**
     Requirements:
     1. The file is a zip file
     2. The file contains no directory
     3. All entries are plain text files
     4. Input and output files must appear in pairs, input files have extension .in and output files have extension .out.
     5. The size of the file is less or equal to 50 MB.
     */
    @SneakyThrows
    public Set<ZipEntry> getValidTestCases(Path zipFile) throws IOException{
        File unzipDestinationDir = new File(zipFile.toString()+".unzip");
        unzipDestinationDir.mkdir();
        ZipFile zip = new ZipFile(zipFile.toString());

        Map<String, ZipEntry> entryParingMap = new HashMap<>();
        Set<ZipEntry> pairedTestCase = new HashSet<>();
        var entries = zip.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            InputStream zipEntryInput = null;
            String entryName = entry.getName();
            String extension = FilenameUtils.getExtension(entryName);
            String baseName = FilenameUtils.getBaseName(entryName);

            // Check directories
            if(FilenameUtils.indexOfLastSeparator(entryName) != -1){
                throw new InvalidTestCaseFileException("Zip file contains directories");
            }

            // Check file type
            zipEntryInput = zip.getInputStream(entry);
            String fileType = tika.detect(zipEntryInput);
            if (!fileType.equals("text/plain")){
                continue;
//                    throw new RuntimeException("File type not supported");
            }
            if (!extension.equals(inputFileExtension) && !extension.equals(outputFileExtension)){
                continue;
            }
            if (entryParingMap.containsKey(baseName)){
                pairedTestCase.add(entryParingMap.get(baseName));
                pairedTestCase.add(entry);
            }
            else{
                entryParingMap.put(baseName, entry);
            }
        }
        return pairedTestCase;

    }

}
