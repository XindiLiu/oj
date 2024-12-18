package com.example.oj.filesystem;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class FileService {
	@Autowired
	FileConfig fileConfig;
	public final static String inputFileExtension = "in";
	public final static String outputFileExtension = "out";

	//    Path tempDirPath = Paths.get(fileConfig.tempDir);
	public void uploadFile(MultipartFile file, String fileName) {
		File dir = new File(fileConfig.rootDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			file.transferTo(new File(dir.getAbsolutePath() + File.separator + fileName));
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
			file.transferTo(new File(dir.getAbsolutePath() + File.separator + fileName));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Path mkTempDir(String subDir, String name) throws IOException {
		Path tempDirPath = Paths.get(fileConfig.tempDir).resolve(subDir); // resolve: add other after this
		Path tempDir = null;
		try {
			Files.createDirectories(tempDirPath);
			tempDir = Files.createTempDirectory(tempDirPath, name);
		} catch (IOException e) {
			log.error("Error creating temp dir {}: {}", tempDirPath, e.getMessage());
			throw new IOException(e);
		}
		return tempDir;
	}

	public Path mkTempDir(String subDir) throws IOException {
		Path tempDirPath = Paths.get(fileConfig.tempDir).resolve(subDir); // resolve: add other after this
		Path tempDir = null;
		try {
			Files.createDirectories(tempDirPath);
			tempDir = Files.createTempDirectory(tempDirPath, null);
		} catch (IOException e) {
			log.error("Error creating temp dir {}: {}", tempDirPath, e.getMessage());
			throw new IOException(e);
		}
		return tempDir;
	}

	public Path mkDir(Path dir) throws IOException {
		Path dirPath = null;
		try {
			dirPath = Files.createDirectories(dir);
		} catch (IOException e) {
			log.error("Error creating dir {}: {}", dir, e.getMessage());
			throw new IOException(e);
		}
		return dirPath;
	}

	public Path copyFile(Path source, Path target) throws IOException {
		Path path = null;
		try {
			if (Files.exists(target)) {
				log.warn("Replace already existing file {}.", target);
			}
			path = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log.error("Error copying from {} to {}: {}", source, target, e.getMessage());
			throw new IOException(e);

		}
		return path;
	}

	public boolean rmDir(Path dir) throws IOException {
		try {
			// From the documentation of Interface FileVisitor<T>
			if (!Files.exists(dir)) {
				return true;
			}
			Files.walkFileTree(dir, new HashSet<>(), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
						throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException e)
						throws IOException {
					if (e == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						// directory iteration failed
						throw new IOException(e);
					}
				}
			});
			return true;
		} catch (IOException e) {
			log.error("Error removing dir {}: {}", dir, e.getMessage());
			throw new IOException(e);
			//			return false;
		}
	}

	public void writeFile(Path path, String input) throws IOException {
		try {
			Files.write(path, input.getBytes());
		} catch (IOException e) {
			log.error("Error writing to file: {}", e.getMessage());
			e.printStackTrace();
			throw new IOException(e);
		}
	}


	public boolean isZip(MultipartFile file) {

		/**
		 * tika.detect() and file.getContentType() seems to work correctly,
		 * Files.probeContentType() seems to have system dependent implementation, gave some different result
		 * URLConnection.guessContentTypeFromStream() does not work
		 * file.getContentType() does not throw IOException
		 */
		//			fileType = tika.detect(file.getInputStream());
		//			fileType = Files.probeContentType(Paths.get(file.getOriginalFilename()));
		//			fileType = URLConnection.guessContentTypeFromStream(file.getInputStream());
		String fileType = file.getContentType();
		return fileType.equals("application/zip") || fileType.equals("application/x-zip-compressed");
	}

	public void saveFile(MultipartFile file, Path path) {
		try {
			file.transferTo(path);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save file", e);
		}
		// TODO: better exception handling
	}

	@Transactional //?
	public Path extractZip(MultipartFile zipFile) throws IOException {

		Path tempDir = null;
		try {
			if (zipFile.isEmpty() || !isZip(zipFile)) {
				throw new IOException("Uploaded file is not a valid ZIP archive.");
			}
			// Temporary directory to extract zip
			tempDir = mkTempDir("zipExtraction", "");
			ZipInputStream zis = new ZipInputStream(zipFile.getInputStream());
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				Path filePath = tempDir.resolve(entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(filePath);
				} else {
					Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
				}
				zis.closeEntry();

			}
		} catch (IOException e) {
			log.error("Error extracting ZIP file: {}", e.getMessage());
			throw new IOException(e);
			//			throw new RuntimeException("Error extracting zip", e);
		}
		return tempDir;

	}

}
