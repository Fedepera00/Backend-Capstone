package it.epicode.patronato_gestionale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PatronatoGestionaleApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatronatoGestionaleApplication.class, args);
	}

}
