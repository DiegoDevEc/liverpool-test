package com.liverpool.liverpooltest.infrastructure.adapter.out.persistence.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDocument {
    private String postalCode;
    private String municipality;
    private String state;
    private String city;
    private List<String> neighborhoods;
    private String country;
}
