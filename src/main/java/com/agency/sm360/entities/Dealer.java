package com.agency.sm360.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor()
@AllArgsConstructor()
@Data
public class Dealer {
    UUID id;
    String name;
}
