package de.adesso.trmclient.cli.commands;

import de.adesso.trmclient.cli.api.enpointwrapper.TimeSheetEndpointWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@RequiredArgsConstructor
@Command
public class TimeSheetCommands {

    private final TimeSheetEndpointWrapper timeSheetEndpointWrapper;

    @Command(command = "create", alias = "c", description = "Create a new Time Sheet", group = "Time Sheet")
    public String createTimesheet(@Option(required = true, longNames = "name", shortNames = 'n', description = "Name of Time Sheet") String name) {
        return timeSheetEndpointWrapper.createTimeSheet(name);
    }

    @Command(command = "list", alias = "l", description = "List all Time Sheets", group = "Time Sheet")
    public String listTimeSheets() {
        return timeSheetEndpointWrapper.listAllTimeSheets();
    }

    @Command(command = "delete", alias = "d", description = "Delete a Time Sheet", group = "Time Sheet")
    public String deleteTimeSheet(@Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Time Sheet to delete") Long id) {
        return timeSheetEndpointWrapper.deleteTimeSheet(id);
    }

    @Command(command = "show", alias = "s", description = "Shows a complete Time Sheet", group = "Time Sheet")
    public String show(@Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Time Sheet to show") Long id) {
        return timeSheetEndpointWrapper.readTimeSheet(id);
    }
}
