package de.adesso.trmclient.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class TrmCliApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrmCliApplication.class, args);
	}
}
