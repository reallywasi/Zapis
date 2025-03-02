package Zapis_Plateform;

import org.springframework.boot.SpringApplication;

public class TestZapisApplication {

	public static void main(String[] args) {
		SpringApplication.from(ZapisApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
