package com.example.reposearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RepoSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepoSearchApplication.class, args);
	}

}
