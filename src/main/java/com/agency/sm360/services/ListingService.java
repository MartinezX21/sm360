package com.agency.sm360.services;

import com.agency.sm360.entities.Dealer;
import com.agency.sm360.utils.TierLimitStrategy;
import com.agency.sm360.config.exceptions.Sm360Exception;
import com.agency.sm360.criteria.ListingCriteria;
import com.agency.sm360.dto.AmendListingRequest;
import com.agency.sm360.entities.Listing;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ListingService {
    public Listing addListing(Listing listing);
    public Listing updateListing(UUID id, AmendListingRequest request) throws Sm360Exception;
    public Listing publishListing(UUID id, TierLimitStrategy strategy) throws Sm360Exception;
    public Listing unPublishListing(UUID id) throws Sm360Exception;
    public Page<Listing> findListings(int page, int size, ListingCriteria criteria);
    public Dealer getDealer(UUID id) throws Sm360Exception;
}
