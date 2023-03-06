package com.agency.sm360.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DealerDTO {
    private String id;
    @NotBlank(message = "Dealer name is mandatory")
    private String name;
}
