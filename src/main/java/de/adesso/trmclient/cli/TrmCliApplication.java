package de.adesso.trmclient.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class TrmCliApplication {
	/*
	 * create sheet "name"
	 * delete sheet id
	 * list sheets
	 *
	 * show sheet
	 * filter?
	 *
	 * book (erstellt eine Buchung mit Tags (nicht gestartet)
	 * start -id [tags]
	 * end -id
	 * modify
	 *
	 *
	 */

	public static void main(String[] args) {
		SpringApplication.run(TrmCliApplication.class, args);
	}

}
