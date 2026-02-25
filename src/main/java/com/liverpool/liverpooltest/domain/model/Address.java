package com.liverpool.liverpooltest.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonDeserialize(builder = Address.AddressBuilder.class)
public class Address {
    private String postalCode;
    private String municipality;
    private String state;
    private String city;
    private List<String> neighborhoods;
    private String country;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AddressBuilder {}
}
