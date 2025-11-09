package ca.qc.cegep.ff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlightsFetcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightsFetcherApplication.class, args);
	}

}
