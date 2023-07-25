package de.adesso.trmclient.cli.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;

@RequiredArgsConstructor
@Command
public class FilterCommand {

    @Command(command = "filter", alias = "f", description = "Filter Time Sheets by name", group = "Time Sheet")
    public String filter() {
        //TODO: TBD
        //Filter TimeSheets or Bookings of One TimeSheet by tags or dates
        return "FILTER";
    }
}
