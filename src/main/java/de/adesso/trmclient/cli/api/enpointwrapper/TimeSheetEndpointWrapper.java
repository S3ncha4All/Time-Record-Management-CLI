package de.adesso.trmclient.cli.api.enpointwrapper;

import de.adesso.trmclient.cli.api.dto.BookingDto;
import de.adesso.trmclient.cli.api.dto.SettingDto;
import de.adesso.trmclient.cli.api.dto.TimeSheetDto;
import de.adesso.trmclient.cli.api.enpointwrapper.model.BookingView;
import de.adesso.trmclient.cli.api.enpointwrapper.model.Tuple;
import org.springframework.beans.BeanWrapperImpl;
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
        builder.on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(10));
        builder.on(CellMatchers.column(0)).addAligner(SimpleHorizontalAligner.center);
        builder.on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(50));
        builder.on(CellMatchers.column(2)).addSizer(new AbsoluteWidthSizeConstraints(12));
        builder.on(CellMatchers.column(2)).addAligner(SimpleHorizontalAligner.center);
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
        builder.on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(10));
        builder.on(CellMatchers.column(0)).addAligner(SimpleHorizontalAligner.center);
        builder.on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(50));
        builder.on(CellMatchers.column(1)).addAligner(SimpleHorizontalAligner.center);
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
            builder.on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(10));
            builder.on(CellMatchers.column(0)).addAligner(SimpleHorizontalAligner.center);
            builder.on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(50));
            builder.on(CellMatchers.column(0)).addAligner(SimpleHorizontalAligner.center);
            builder.on(CellMatchers.column(2)).addSizer(new AbsoluteWidthSizeConstraints(50));
            builder.on(CellMatchers.column(0)).addAligner(SimpleHorizontalAligner.center);
            output += builder.build().render(settings.size());
            output += "\n";
        }
        if(dto.getBookings() != null && !dto.getBookings().isEmpty()) {
            List<BookingDto> dtoBookings = dto.getBookings();
            List<BookingView> bookings = dtoBookings.stream().map(
                    b -> {
                        BookingView bv = new BookingView();
                        bv.setId(b.getId());
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
                        bv.setBegin(b.getBegin()!=null?b.getBegin().format(dtf):"");
                        bv.setEnd(b.getEnd()!=null?b.getEnd().format(dtf):"");
                        String duration = "";
                        if(b.getBegin() == null) {
                            duration = "not started";
                        } else {
                            if(b.getEnd() == null) {
                                duration = "ongoing";
                            } else {
                                long seconds = b.getBegin().until(b.getEnd(), ChronoUnit.SECONDS);
                                duration = ""+seconds;
                            }
                        }
                        bv.setDuration(duration);
                        StringBuilder sb = new StringBuilder();
                        b.getTags().forEach(t -> sb.append("#"+t.getName()+" "));
                        bv.setTags(sb.toString());
                        return bv;
                    }
            ).toList();
            headers = new LinkedHashMap<>();
            headers.put("id", "ID:");
            headers.put("begin", "Start:");
            headers.put("end", "End:");
            headers.put("duration", "Duration(in Hours):");
            headers.put("tags", "Tags:");
            model = new BeanListTableModel<>(bookings, headers);
            builder = buildTable(model);
            builder.on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(10));
            builder.on(CellMatchers.column(0)).addAligner(SimpleHorizontalAligner.center);
            builder.on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(20));
            builder.on(CellMatchers.column(1)).addAligner(SimpleHorizontalAligner.center);
            builder.on(CellMatchers.column(2)).addSizer(new AbsoluteWidthSizeConstraints(20));
            builder.on(CellMatchers.column(2)).addAligner(SimpleHorizontalAligner.center);
            builder.on(CellMatchers.column(3)).addSizer(new AbsoluteWidthSizeConstraints(22));
            builder.on(CellMatchers.column(3)).addAligner(SimpleHorizontalAligner.center);
            builder.on(CellMatchers.column(4)).addSizer(new AbsoluteWidthSizeConstraints(100));
            output += builder.build().render(bookings.size());
        }
        return output;
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
