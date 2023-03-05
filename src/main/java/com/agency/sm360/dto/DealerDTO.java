package com.agency.sm360.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DealerDTO {
    private String id;
    @NotBlank(message = "Dealer name is mandatory")
    private String name;
}
