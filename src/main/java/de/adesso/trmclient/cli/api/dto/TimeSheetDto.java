package de.adesso.trmclient.cli.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSheetDto {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @JsonProperty(value = "booking_count", defaultValue = "0")
    private int bookingCount;

    private List<SettingDto> settings;

    private List<BookingDto> bookings;

}