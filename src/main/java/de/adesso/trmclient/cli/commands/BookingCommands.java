package de.adesso.trmclient.cli.commands;

import de.adesso.trmclient.cli.api.enpointwrapper.BookingEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@RequiredArgsConstructor
@Command
public class BookingCommands {

    private final BookingEndpoint bookingEndpoint;

    @Command(command = "book", alias = "b", description = "Create Booking with Tags", group = "Booking")
    public String book(
            @Option(required = true, longNames = "timeSheetId", shortNames = 'i', description = "ID of Time Sheet") Long timeSheetId,
            @Option(longNames = "activate", shortNames = 'a', description = "Activate Booking directly") Boolean activate,
            @Option(longNames = "tags", shortNames = 't', description = "Tags for this Booking") String[] tags
    ) {
        return bookingEndpoint.book(timeSheetId, activate, tags);
    }

    @Command(command = "start", alias = "s", description = "Start Booking on Time Sheet with ID", group = "Booking")
    public String start(@Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Booking") Long id) {
        return bookingEndpoint.startBooking(id);
    }

    @Command(command = "end", alias = "e", description = "End Booking with ID", group = "Booking")
    public String end(@Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Booking") Long id) {
        return bookingEndpoint.endBooking(id);
    }

    @Command(command = "remove", alias = "r", description = "Remove Booking with ID", group = "Booking")
    public String remove(@Option(required = true, longNames = "id", shortNames = 'i', description = "ID of Booking") Long id) {
        return bookingEndpoint.removeBooking(id);
    }
}
