package de.adesso.trmclient.cli.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@RequiredArgsConstructor
@Command
public class BookingCommands {

    @Command(command = "book", alias = "b", description = "Create Booking with Tags", group = "Booking")
    public String book() {
        return "book";
    }

    @Command(command = "start", alias = "s", description = "Start Booking with ID", group = "Booking")
    public String start(@Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Booking") Long id) {
        return "start";
    }

    @Command(command = "end", alias = "e", description = "End Booking with ID", group = "Booking")
    public String end(@Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Booking") Long id) {
        return "end";
    }

    @Command(command = "modify", alias = "m", description = "Modify Booking", group = "Booking")
    public String modify(@Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Booking") Long id) {
        return "modify";
    }
}
