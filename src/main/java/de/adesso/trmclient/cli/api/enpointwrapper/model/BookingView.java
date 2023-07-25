package de.adesso.trmclient.cli.api.enpointwrapper.model;

import de.adesso.trmclient.cli.api.dto.TagReadDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingView {

    @NotNull
    private Long id;

    private String begin;

    private String end;

    private String tags;
}
