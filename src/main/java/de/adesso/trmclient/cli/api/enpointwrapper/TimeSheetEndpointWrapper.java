package de.adesso.trmclient.cli.api.enpointwrapper;

import de.adesso.trmclient.cli.api.dto.BookingDto;
import de.adesso.trmclient.cli.api.dto.SettingDto;
import de.adesso.trmclient.cli.api.dto.TimeSheetDto;
import de.adesso.trmclient.cli.api.enpointwrapper.model.BookingView;
import de.adesso.trmclient.cli.api.enpointwrapper.model.Tuple;
import org.springframework.http.HttpMethod;
import org.springframework.shell.table.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class TimeSheetEndpointWrapper extends BaseEndpointWrapper<TimeSheetDto> {

    public String createTimeSheet(String name) {
        Optional<TimeSheetDto> dtoOpt = request(HttpMethod.POST, "http://localhost:8080/api/v1/time-sheet", new Tuple("name", name));
        TimeSheetDto ts = dtoOpt.get();
        return "TimeSheet created (ID: "+ts.getId()+").";
    }

    public String listAllTimeSheets() {
        List<TimeSheetDto> ts = requestList(HttpMethod.GET, "http://localhost:8080/api/v1/time-sheet");
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID:");
        headers.put("name", "Name:");
        headers.put("bookingCount", "Bookings:");
        TableModel model = new BeanListTableModel<>(ts, headers);
        TableBuilder builder = buildTable(model);
        constrain(builder, 10, 50, 12);
        constrain(builder, SimpleHorizontalAligner.center, SimpleHorizontalAligner.left, SimpleHorizontalAligner.center);
        return builder.build().render(2);
    }

    public String readTimeSheet(Long id) {
        StringBuilder output = new StringBuilder();
        Optional<TimeSheetDto> optDto = request(HttpMethod.GET, "http://localhost:8080/api/v1/time-sheet/"+id);
        TimeSheetDto dto = optDto.get();
        output.append(displayTimeSheetSettings(dto));
        output.append("\n");
        output.append(displayTimeSheetBookings(dto));
        output.append("\n");
        output.append(displayTimeSheetHead(dto));
        return output.toString();
    }

    private String displayTimeSheetHead(TimeSheetDto dto) {
        TableModel model = new ArrayTableModel(new Object[][]{
                {"id:", dto.getId()},
                {"name:", dto.getName()},
                {"Overall Time booked(calculated):", calculateOverallTime(dto.getBookings())}
        });
        TableBuilder builder = buildTable(model);
        constrain(builder, 40, 80);
        constrain(builder, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center);
        return builder.build().render(2);
    }

    private String displayTimeSheetSettings(TimeSheetDto dto) {
        if(dto.getSettings() == null || dto.getSettings().isEmpty()) return "";
        List<SettingDto> settings = dto.getSettings();
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID:");
        headers.put("name", "Key:");
        headers.put("value", "Value:");
        TableModel model = new BeanListTableModel<>(settings, headers);
        TableBuilder builder = buildTable(model);
        constrain(builder, 10, 50, 50);
        constrain(builder, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center);
        return builder.build().render(settings.size());
    }

    private String displayTimeSheetBookings(TimeSheetDto dto) {
        if(dto.getBookings() == null || dto.getBookings().isEmpty()) return "";
        List<BookingDto> dtoBookings = dto.getBookings();
        List<BookingView> bookings = dtoBookings.stream().map(this::createView).toList();
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID:");
        headers.put("begin", "Start:");
        headers.put("end", "End:");
        headers.put("duration", "Duration(in Hours):");
        headers.put("tags", "Tags:");
        TableModel model = new BeanListTableModel<>(bookings, headers);
        TableBuilder builder = buildTable(model);
        constrain(builder, 10, 20, 20, 22, 100);
        constrain(builder, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center);
        return builder.build().render(bookings.size());
    }

    private String calculateOverallTime(List<BookingDto> bookings) {
        long seconds = 0L;
        if(bookings != null) {
            for(BookingDto booking : bookings) {
                seconds += calculateDuration(booking.getBegin(), booking.getEnd());
            }
        }
        return formatSeconds(seconds);
    }

    private BookingView createView(BookingDto b) {
        BookingView bv = new BookingView();
        bv.setId(b.getId());
        bv.setBegin(dateView(b.getBegin()));
        bv.setEnd(dateView(b.getEnd()));
        bv.setDuration(formatCalculatedDuration(b.getBegin(), b.getEnd()));
        StringBuilder sb = new StringBuilder();
        b.getTags().forEach(t -> sb.append("#").append(t.getName()).append(" "));
        bv.setTags(sb.toString());
        return bv;
    }

    private String dateView(LocalDateTime date) {
        if(date == null) return "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
        return date.format(dtf);
    }

    private String formatCalculatedDuration(LocalDateTime begin, LocalDateTime end) {
        if(begin == null) return "not started";
        String suffix = "";
        if(end == null) {
            suffix = " (ongoing)";
        }
        long seconds = calculateDuration(begin, end);
        return formatSeconds(seconds, suffix);
    }

    private String formatSeconds(long seconds) {
        return this.formatSeconds(seconds, "");
    }

    private String formatSeconds(long seconds, String suffix) {
        long hh = seconds / 3600;
        long mm = (seconds % 3600) / 60;
        return String.format("%02d:%02d%s", hh, mm, suffix);
    }

    private long calculateDuration(LocalDateTime begin, LocalDateTime end) {
        if(begin == null) return 0L;
        if(end == null) {
            end = LocalDateTime.now();
        }
        return begin.until(end, ChronoUnit.SECONDS);
    }

    private void constrain(TableBuilder builder, int... constraints) {
        for(int i = 0; i<constraints.length; i++) {
            builder.on(CellMatchers.column(i)).addSizer(new AbsoluteWidthSizeConstraints(constraints[i]));
        }
    }

    private void constrain(TableBuilder builder, Aligner... alignments) {
        for(int i = 0; i<alignments.length; i++) {
            builder.on(CellMatchers.column(i)).addAligner(alignments[i]);
        }
    }

    private TableBuilder buildTable(TableModel model) {
        TableBuilder builder = new TableBuilder(model);
        builder.addOutlineBorder(BorderStyle.fancy_heavy);
        builder.addInnerBorder(BorderStyle.fancy_light);
        builder.addHeaderBorder(BorderStyle.fancy_double);
        return builder;
    }

    public String deleteTimeSheet(Long id) {
        request(HttpMethod.DELETE, "http://localhost:8080/api/v1/time-sheet/"+id);
        return "TimeSheet deleted.";
    }

    @Override
    protected Class<TimeSheetDto> getGenericClass() {
        return TimeSheetDto.class;
    }
}
