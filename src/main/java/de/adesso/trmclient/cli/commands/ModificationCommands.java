package de.adesso.trmclient.cli.commands;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(command = "modify", alias = {"modify", "m"}, description = "Modifications")
public class ModificationCommands {

    @Command(command = "timesheet", alias = {"timesheet", "ts"}, description = "Rename Time Sheet", group = "Time Sheet")
    public String modTimeSheet(
            @Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Time Sheet to rename") Long id,
            @Option(longNames = "name", shortNames = 'n', description = "New Name of Timesheet", required = true) String name
    ) {

        return "MODIFY Time Sheet";
    }

    @Command(command = "booking", alias = {"booking", "b"}, group = "Booking")
    public String modBooking(
            @Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Booking") Long id
    ) {

        return "MODIFY Booking";
    }

    @Command(command = "tag", alias = {"t", "tag"}, group = "Booking")
    public String modTag(
            @Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Booking") Long id,
            @Option(required = true, longNames = "tags", shortNames = 't', description = "Tags for this Booking") String[] tags
    ) {

        return "MODIFY Booking Tags (add/remove)";
    }
}
