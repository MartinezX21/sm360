package com.agency.sm360.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AmendListingRequest {
    private String vehicle;
    private Double price;
}
