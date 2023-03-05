package com.agency.sm360.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedListings {
    private int page;
    private int totalPages;
    private long totalElements;
    private List<ListingDTO> content;
}
