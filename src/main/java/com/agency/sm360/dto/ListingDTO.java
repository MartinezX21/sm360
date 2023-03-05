package com.agency.sm360.dto;

import com.agency.sm360.utils.ListingState;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ListingDTO {
    private String id;
    private DealerDTO dealer;
    private String vehicle;
    private Double price;
    private Date createdAt;
    private ListingState state;
}
