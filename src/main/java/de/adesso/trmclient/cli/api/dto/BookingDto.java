package de.adesso.trmclient.cli.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;

    private LocalDateTime begin;

    private LocalDateTime end;

    private List<TagReadDto> tags;

}
