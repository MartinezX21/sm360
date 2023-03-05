package com.agency.sm360.services;

import com.agency.sm360.entities.Dealer;
import com.agency.sm360.utils.ListingState;
import com.agency.sm360.utils.TierLimitStrategy;
import com.agency.sm360.config.AppProperties;
import com.agency.sm360.config.exceptions.Sm360Exception;
import com.agency.sm360.criteria.ListingCriteria;
import com.agency.sm360.criteria.ListingSpecs;
import com.agency.sm360.dao.DealerDAO;
import com.agency.sm360.dao.ListingDAO;
import com.agency.sm360.dto.AmendListingRequest;
import com.agency.sm360.entities.Listing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ListingServiceImpl implements ListingService {
    private final DealerDAO dealerDAO;
    private final ListingDAO listingDAO;
    private final AppProperties appProperties;

    @Autowired
    public ListingServiceImpl(DealerDAO dealerDAO, ListingDAO listingDAO, AppProperties appProperties) {
        this.dealerDAO = dealerDAO;
        this.listingDAO = listingDAO;
        this.appProperties = appProperties;
    }
    @Override
    public Listing addListing(Listing listing) {
        if(listing.getDealer().getId() == null || !dealerDAO.existsById(listing.getDealer().getId())) {
            // we create the dealer on the flight if not existing
            listing.setDealer(dealerDAO.save(listing.getDealer()));
        }
        listing.setState(ListingState.draft);
        listing.setCreatedAt(Calendar.getInstance().getTime());
        return listingDAO.save(listing);
    }

    @Override
    public Listing updateListing(UUID id, AmendListingRequest request)  throws Sm360Exception {
        try {
            // find the listing by the given id and throw a NoSuchElementException if not found
            Listing listing = listingDAO.findById(id).orElseThrow();
            // apply the patch and save the listing
            listing.setVehicle(request.getVehicle());
            listing.setPrice(request.getPrice());
            return listingDAO.save(listing);
        } catch (NoSuchElementException ex) {
            Sm360Exception e = new Sm360Exception(ex);
            e.setDetails(id);
            throw e;
        }
    }

    @Override
    public Listing publishListing(UUID id, TierLimitStrategy strategy) throws Sm360Exception {
        try {
            // find the listing by the given id and throw a NoSuchElementException if not found
            Listing listing = listingDAO.findById(id).orElseThrow();
            //
            if(listing.getState() == ListingState.published) {
                return listing;
            }
            Listing oldestPublished = null;
            int totalPublished = listingDAO.countByDealerAndState(listing.getDealer(), ListingState.published);
            if(totalPublished >= appProperties.getTierLimit()) {
                if (strategy == TierLimitStrategy.unpublish_oldest) {
                    oldestPublished = listingDAO.findOldestByState(ListingState.published).orElse(null);
                    if (oldestPublished == null) {
                        throw new Sm360Exception("Unexpected error occurred. Please try later");
                    }
                    oldestPublished.setState(ListingState.draft);
                } else {
                    // cancel_with_error -- default strategy
                    // cancel the operation and throw an exception
                    throw new Sm360Exception("Tier limit is reached");
                }
            }
            listing.setState(ListingState.published);
            if(oldestPublished != null) {
                return listingDAO.saveAll(Arrays.asList(oldestPublished, listing)).stream()
                        .filter((Listing it) -> it.getId().equals(listing.getId()))
                        .findFirst().orElse(listing);
            } else {
                return listingDAO.save(listing);
            }
        } catch (NoSuchElementException ex) {
            Sm360Exception e = new Sm360Exception(ex);
            e.setDetails(id);
            throw e;
        }
    }

    @Override
    public Listing unPublishListing(UUID id) throws Sm360Exception {
        try {
            // find the listing by the given id and throw a NoSuchElementException if not found
            Listing listing = listingDAO.findById(id).orElseThrow();
            //
            listing.setState(ListingState.draft);
            return listingDAO.save(listing);
        } catch (NoSuchElementException ex) {
            Sm360Exception e = new Sm360Exception(ex);
            e.setDetails(id);
            throw e;
        }
    }

    @Override
    public Page<Listing> findListings(int page, int size, ListingCriteria criteria) {
        Specification<Listing> specs = null;
        if(criteria.getDealer() != null) {
            specs = ListingSpecs.dealerIs(criteria.getDealer());
        }
        if(criteria.getState() != null) {
            specs = specs == null? ListingSpecs.stateIs(criteria.getState()) :
                    specs.and(ListingSpecs.stateIs(criteria.getState()));
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Listing> data = specs == null? listingDAO.findAll(pageable) : listingDAO.findAll(specs, pageable);
        return data;
    }

    @Override
    public Dealer getDealer(UUID id) throws Sm360Exception {
        try {
            // find the dealer by the given id and throw a NoSuchElementException if not found
            return dealerDAO.findById(id).orElseThrow();
        } catch (NoSuchElementException ex) {
            Sm360Exception e = new Sm360Exception(ex);
            e.setDetails(id);
            throw e;
        }
    }

}
