package com.agency.sm360.controllers;

import com.agency.sm360.entities.Listing;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(produces="application/json")
public class ListingController {

    @GetMapping("/listings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Listing getListings(@PathVariable String id) {
        Listing listing = new Listing();
        listing.setId(UUID.randomUUID());
        return listing;
    }
}
