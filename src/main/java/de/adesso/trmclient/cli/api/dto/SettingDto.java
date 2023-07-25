package de.adesso.trmclient.cli.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingDto {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private String value;

}
