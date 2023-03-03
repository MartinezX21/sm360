package com.agency.sm360.entities;

import com.agency.sm360.common.ListingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Listing {
    UUID id;
    String vehicle;
    Double price;
    Date createdAt;
    ListingStatus state;
}
