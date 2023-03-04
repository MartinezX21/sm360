package com.agency.sm360.entities;

import com.agency.sm360.common.ListingState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(optional=false)
    Dealer dealer;
    String vehicle;
    Double price;
    @Temporal(TemporalType.TIMESTAMP)
    Date createdAt;
    @Enumerated(EnumType.STRING)
    ListingState state;
}
