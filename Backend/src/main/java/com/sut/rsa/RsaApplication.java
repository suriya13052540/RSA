package com.sut.rsa;

import com.sut.rsa.Service.StorageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;

@SpringBootApplication
public class RsaApplication implements CommandLineRunner {

	@Resource
	StorageService storageService;

	public static void main (String[] args) {
		SpringApplication.run(RsaApplication.class, args);
	}

	@Override
	public void run(String... arg) throws Exception {
		storageService.deleteAll();
		storageService.init();
		storageService.deleteAlldecryp();
		storageService.initdecryp();
	}

}
