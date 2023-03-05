package com.agency.sm360.controllers;

import com.agency.sm360.config.exceptions.Sm360Exception;
import com.agency.sm360.criteria.ListingCriteria;
import com.agency.sm360.dto.*;
import com.agency.sm360.entities.Dealer;
import com.agency.sm360.entities.Listing;
import com.agency.sm360.services.ListingService;
import com.agency.sm360.utils.Converters;
import com.agency.sm360.utils.ListingState;
import com.agency.sm360.utils.TierLimitStrategy;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(produces = "application/json")
public class ListingController {
    private final ModelMapper mapper;
    private final ListingService listingService;

    @Autowired
    public ListingController(ModelMapper mapper, ListingService listingService) {
        this.mapper = mapper;
        this.listingService = listingService;
        //
        // model mapper setup
        //
        // -- DealerDTO to Dealer
        TypeMap<DealerDTO, Dealer> dealerPropertyMapper1 = this.mapper.createTypeMap(DealerDTO.class, Dealer.class);
        dealerPropertyMapper1.addMappings(_mapper -> _mapper.using(Converters.stringToUuidConverter).map(DealerDTO::getId, Dealer::setId));
        // -- Dealer to DealerDTO
        TypeMap<Dealer, DealerDTO> dealerPropertyMapper2 = this.mapper.createTypeMap(Dealer.class, DealerDTO.class);
        dealerPropertyMapper2.addMappings(_mapper -> _mapper.using(Converters.uuidToStringConverter).map(Dealer::getId, DealerDTO::setId));
        //
        // -- ListingDTO to Listing
        TypeMap<ListingDTO, Listing> listingPropertyMapper1 = this.mapper.createTypeMap(ListingDTO.class, Listing.class);
        listingPropertyMapper1.addMappings(_mapper -> _mapper.using(Converters.stringToUuidConverter).map(ListingDTO::getId, Listing::setId));
        // -- Listing to ListingDTO
        TypeMap<Listing, ListingDTO> listingPropertyMapper2 = this.mapper.createTypeMap(Listing.class, ListingDTO.class);
        listingPropertyMapper2.addMappings(_mapper -> _mapper.using(Converters.uuidToStringConverter).map(Listing::getId, ListingDTO::setId));
    }

    @PostMapping("/listings")
    @ResponseStatus(HttpStatus.CREATED)
    public ListingDTO addListing(@Valid @RequestBody CreateListingRequest request) {
        Listing listing = this.mapper.map(request, Listing.class);
        listing = listingService.addListing(listing);
        return this.mapper.map(listing, ListingDTO.class);
    }

    @PutMapping("/listings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ListingDTO updateListing(
            @PathVariable("id") String id,
            @Valid @RequestBody AmendListingRequest request) throws Sm360Exception {
        Listing listing = listingService.updateListing(UUID.fromString(id), request);
        return this.mapper.map(listing, ListingDTO.class);
    }

    @GetMapping("/listings")
    @ResponseStatus(HttpStatus.OK)
    public PaginatedListings getListings(
            @RequestParam(name = "dealer_id", required = false) String dealerId,
            @RequestParam(name = "state", required = false) ListingState state,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "100") int size
    ) throws Sm360Exception {
        ListingCriteria criteria = new ListingCriteria();
        if(dealerId != null) {
            criteria.setDealer(listingService.getDealer(UUID.fromString(dealerId)));
        }
        if(state != null) {
            criteria.setState(state);
        }
        Page<Listing> listings = listingService.findListings(page, size, criteria);
        return new PaginatedListings(
                page,
                listings.getTotalPages(),
                listings.getTotalElements(),
                listings.getContent().stream()
                        .map(it -> this.mapper.map(it, ListingDTO.class))
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/listings/{id}/publish")
    @ResponseStatus(HttpStatus.OK)
    public ListingDTO publishListing(
            @PathVariable("id") String id,
            @RequestParam(name = "strategy", required = false) TierLimitStrategy strategy) throws Sm360Exception {
        Listing listing = listingService.publishListing(UUID.fromString(id), strategy != null? strategy : TierLimitStrategy.cancel_with_error);
        return this.mapper.map(listing, ListingDTO.class);
    }

    @PutMapping("/listings/{id}/unpublish")
    @ResponseStatus(HttpStatus.OK)
    public ListingDTO unPublishListing(@PathVariable("id") String id) throws Sm360Exception {
        Listing listing = listingService.unPublishListing(UUID.fromString(id));
        return this.mapper.map(listing, ListingDTO.class);
    }
}
