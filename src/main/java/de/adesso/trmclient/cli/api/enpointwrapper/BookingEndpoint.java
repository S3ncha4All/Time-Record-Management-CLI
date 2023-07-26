package de.adesso.trmclient.cli.api.enpointwrapper;

import de.adesso.trmclient.cli.api.dto.BookingDto;
import de.adesso.trmclient.cli.api.enpointwrapper.model.Tuple;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;

@Component
public class BookingEndpoint extends BaseEndpointWrapper<BookingDto>{

    public String book(Long timeSheetId, Boolean activate, String[] tags) {
        if(activate == null) {
            activate = false;
        }
        if(tags == null) {
            tags = new String[]{};
        }
        LinkedHashMap<String, Object> tagMap = new LinkedHashMap<>();
        tagMap.put("list_tag_name", Arrays.stream(tags).toList());
        Optional<BookingDto> dtoOpt = request(HttpMethod.POST, "http://localhost:8080/api/v1/booking",
                new Tuple("time_sheet_id", timeSheetId),
                new Tuple("active_right_away", activate),
                new Tuple("tags", tagMap));
        BookingDto dto = dtoOpt.get();
        return "Booking created (ID: "+dto.getId()+").";
    }

    public String startBooking(Long id) {
        Optional<BookingDto> dtoOpt = request(HttpMethod.PATCH, "http://localhost:8080/api/v1/booking/"+id+"/start");
        BookingDto dto = dtoOpt.get();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
        String start = dto.getBegin().format(dtf);
        return "Booking Start: "+start;
    }

    public String endBooking(Long id) {
        Optional<BookingDto> dtoOpt = request(HttpMethod.PATCH, "http://localhost:8080/api/v1/booking/"+id+"/end");
        BookingDto dto = dtoOpt.get();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");
        String end = dto.getEnd().format(dtf);
        return "Booking End: "+end;
    }

    public String removeBooking(Long id) {
        request(HttpMethod.DELETE, "http://localhost:8080/api/v1/booking/"+id);
        return "Booking removed.";
    }

    @Override
    protected Class<BookingDto> getGenericClass() {
        return BookingDto.class;
    }
}
