package com.example.oj.filesystem;

import com.example.oj.exception.InvalidTestCaseFileException;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
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

	public boolean isZip(Path path) {
		String fileType = null;
		try {
			fileType = tika.detect(path);
		} catch (IOException e) {
			log.warn("Could not determine MIME type for file: {}", path.toString());
			return false;
		}
		return fileType.equals("application/zip");
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
		return fileType.equals("application/zip");
	}

	public String type(MultipartFile file) {
		File dir = new File("data");

		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(dir.getAbsolutePath() + File.separator + file.getName());
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
	 * Requirements:
	 * 1. The file is a zip file
	 * 2. The file contains no directory
	 * 3. All entries are plain text files
	 * 4. Input and output files must appear in pairs, input files have extension .in and output files have extension .out.
	 * 5. The size of the file is less or equal to 50 MB.
	 */
	@SneakyThrows
	public Set<ZipEntry> getValidTestCases(Path zipFile) throws IOException {
		File unzipDestinationDir = new File(zipFile.toString() + ".unzip");
		unzipDestinationDir.mkdir();
		ZipFile zip = new ZipFile(zipFile.toString());

		Map<String, ZipEntry> entryParingMap = new HashMap<>();
		Set<ZipEntry> pairedTestCase = new HashSet<>();
		var entries = zip.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			InputStream zipEntryInput = null;
			String entryName = entry.getName();
			String extension = FilenameUtils.getExtension(entryName).toLowerCase();
			String baseName = FilenameUtils.getBaseName(entryName).toLowerCase();

			// Check directories
			if (FilenameUtils.indexOfLastSeparator(entryName) != -1) {
				throw new InvalidTestCaseFileException("Zip file contains directories");
			}

			// Check file type
			zipEntryInput = zip.getInputStream(entry);
			String fileType = tika.detect(zipEntryInput);
			if (!fileType.equals("text/plain")) {
				continue;
				//                    throw new RuntimeException("File type not supported");
			}
			if (!extension.equals(inputFileExtension) && !extension.equals(outputFileExtension)) {
				continue;
			}
			if (entryParingMap.containsKey(baseName)) {
				pairedTestCase.add(entryParingMap.get(baseName));
				pairedTestCase.add(entry);
			} else {
				entryParingMap.put(baseName, entry);
			}
		}
		return pairedTestCase;

	}

	public void saveFile(MultipartFile file, Path path) {
		try {
			file.transferTo(path);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save file", e);
		}
		// TODO: better exception handling
	}

	@Transactional
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
