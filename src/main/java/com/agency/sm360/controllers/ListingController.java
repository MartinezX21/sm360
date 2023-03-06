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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(produces = "application/json")
@Tag(name = "Listing", description = "Endpoints for managing listings for online advertising service")
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
    @Operation(
            summary = "Adds a new listing",
            description = "Every created listing has a state draft by default. If the corresponding dealer doesn't exists, its id should be ignored and it will be created on the flight",
            tags = { "Listing" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "201",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListingDTO.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ListingDTO addListing(@Valid @RequestBody CreateListingRequest request) {
        Listing listing = this.mapper.map(request, Listing.class);
        listing = listingService.addListing(listing);
        return this.mapper.map(listing, ListingDTO.class);
    }

    @PutMapping("/listings/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Updates a listing",
            description = "You can change the vehicle or the price of an existing listing. These values override the existing ones. If a property is not present in the amendment request, the corresponding value will be cleared",
            tags = { "Listing" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListingDTO.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ListingDTO updateListing(
            @Parameter(description = "The id of the listing to be updated")
            @PathVariable("id") String id,
            @Valid @RequestBody AmendListingRequest request) throws Sm360Exception {
        Listing listing = listingService.updateListing(UUID.fromString(id), request);
        return this.mapper.map(listing, ListingDTO.class);
    }

    @GetMapping("/listings")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Finds listings",
            description = "Search for listing according the the following optional criteria: dealer, state",
            tags = { "Listing" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaginatedListings.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
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
    @Operation(
            summary = "Publishes a listing",
            description = "The dealer configuration is base on the strategy parameter",
            tags = { "Listing" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListingDTO.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Unprocessable entity", responseCode = "422", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ListingDTO publishListing(
            @Parameter(description = "The id of the listing to be published")
            @PathVariable("id") String id,
            @Parameter(description = "Tier limit strategy. Use cancel_with_error to prevent the listing to be published in case the tier limit is reached. Use unpublish_oldest to unpublish the oldest published listing and publish the current one in case the tier limit is reached")
            @RequestParam(name = "strategy", required = false) TierLimitStrategy strategy) throws Sm360Exception {
        Listing listing = listingService.publishListing(UUID.fromString(id), strategy != null? strategy : TierLimitStrategy.cancel_with_error);
        return this.mapper.map(listing, ListingDTO.class);
    }

    @PutMapping("/listings/{id}/unpublish")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Unpublishes a listing",
            description = "If the listing is in draft state, nothing is done and it is simply returned back as response",
            tags = { "Listing" },
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListingDTO.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Unprocessable entity", responseCode = "422", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ListingDTO unPublishListing(
            @Parameter(description = "The id of the listing to be unpublished")
            @PathVariable("id") String id) throws Sm360Exception {
        Listing listing = listingService.unPublishListing(UUID.fromString(id));
        return this.mapper.map(listing, ListingDTO.class);
    }
}
