package com.agency.sm360.criteria;

import com.agency.sm360.utils.ListingState;
import com.agency.sm360.entities.Dealer;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListingCriteria {
    private Dealer dealer;
    private ListingState state;
}
