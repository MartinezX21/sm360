package com.agency.sm360.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateListingRequest {
    @NotNull(message = "dealer is mandatory")
    private DealerDTO dealer;
    @NotBlank(message = "vehicle is mandatory")
    private String vehicle;
    @NotNull(message = "price is mandatory")
    private Double price;
}
