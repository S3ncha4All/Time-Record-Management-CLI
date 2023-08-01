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
        String output = "";
        Optional<TimeSheetDto> optDto = request(HttpMethod.GET, "http://localhost:8080/api/v1/time-sheet/"+id);
        TimeSheetDto dto = optDto.get();
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID:");
        headers.put("name", "Name:");
        TableModel model = new ArrayTableModel(new Object[][]{
                {"id", dto.getId()},
                {"name", dto.getName()}
        });
        TableBuilder builder = buildTable(model);
        constrain(builder, 10, 50);
        constrain(builder, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center);
        output += builder.build().render(2);
        output += "\n";
        if(dto.getSettings() != null && !dto.getSettings().isEmpty()) {
            List<SettingDto> settings = dto.getSettings();
            headers = new LinkedHashMap<>();
            headers.put("id", "ID:");
            headers.put("name", "Key:");
            headers.put("value", "Value:");
            model = new BeanListTableModel<>(settings, headers);
            builder = buildTable(model);
            constrain(builder, 10, 50, 50);
            constrain(builder, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center);
            output += builder.build().render(settings.size());
            output += "\n";
        }
        if(dto.getBookings() != null && !dto.getBookings().isEmpty()) {
            List<BookingDto> dtoBookings = dto.getBookings();
            List<BookingView> bookings = dtoBookings.stream().map(this::createView).toList();
            headers = new LinkedHashMap<>();
            headers.put("id", "ID:");
            headers.put("begin", "Start:");
            headers.put("end", "End:");
            headers.put("duration", "Duration(in Hours):");
            headers.put("tags", "Tags:");
            model = new BeanListTableModel<>(bookings, headers);
            builder = buildTable(model);
            constrain(builder, 10, 20, 20, 22, 100);
            constrain(builder, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center, SimpleHorizontalAligner.center);
            output += builder.build().render(bookings.size());
        }
        return output;
    }

    private BookingView createView(BookingDto b) {
        BookingView bv = new BookingView();
        bv.setId(b.getId());
        bv.setBegin(dateView(b.getBegin()));
        bv.setEnd(dateView(b.getEnd()));
        bv.setDuration(calculateDuration(b.getBegin(), b.getEnd()));
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

    private String calculateDuration(LocalDateTime begin, LocalDateTime end) {
        if(begin == null) return "not started";
        String suffix = "";
        if(end == null) {
            end = LocalDateTime.now();
            suffix = " (ongoing)";
        }
        long seconds = begin.until(end, ChronoUnit.SECONDS);
        long hh = seconds / 3600;
        long mm = (seconds % 3600) / 60;
        return String.format("%02d:%02d%s", hh, mm, suffix);
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
