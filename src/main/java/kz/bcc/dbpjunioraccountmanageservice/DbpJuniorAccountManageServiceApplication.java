package kz.bcc.dbpjunioraccountmanageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DbpJuniorAccountManageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbpJuniorAccountManageServiceApplication.class, args);
	}

}
