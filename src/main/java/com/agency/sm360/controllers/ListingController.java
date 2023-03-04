package com.agency.sm360.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ListingController {

    @GetMapping("/listings")
    public String getListings() {
        return "It works!";
    }
}
