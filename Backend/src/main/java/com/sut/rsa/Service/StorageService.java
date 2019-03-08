package com.sut.rsa.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
 
@Service
public class StorageService {
 
	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private final Path rootLocation = Paths.get("file");
	private final Path rootLocationdecryp = Paths.get("decryp");

	public void store(MultipartFile file) {
		try {
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
		} catch (Exception e) {
			throw new RuntimeException("FAIL!");
		}
	}

	public void storeDecryp(MultipartFile file) {
		try {
			Files.copy(file.getInputStream(), this.rootLocationdecryp.resolve(file.getOriginalFilename()));
		} catch (Exception e) {
			throw new RuntimeException("FAIL!");
		}
	}
 
	public Resource loadFile(String filename) {
		try {
			Path file = this.rootLocation.resolve(filename).normalize();
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new RuntimeException("FAIL!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("FAIL!");
		}
	}
 
	public void deleteAlldecryp() {
		FileSystemUtils.deleteRecursively(rootLocationdecryp.toFile());
		//FileSystemUtils.deleteRecursively(rootLocation);
		//FileSystemUtils.


	}

	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
		//FileSystemUtils.deleteRecursively(rootLocation);
		//FileSystemUtils.


	}
 
	public void init() {
		try {
			Files.createDirectory(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage!");
		}
	}

	public void initdecryp() {
		try {
			Files.createDirectory(rootLocationdecryp);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage!");
		}
	}
}