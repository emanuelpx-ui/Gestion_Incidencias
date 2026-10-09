package com.devdesk.Gestion_Incidencias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GestionIncidenciasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionIncidenciasApplication.class, args);
	}

}
