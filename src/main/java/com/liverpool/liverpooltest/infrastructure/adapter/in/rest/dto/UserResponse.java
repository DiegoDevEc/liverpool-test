package com.liverpool.liverpooltest.infrastructure.adapter.in.rest.dto;

import com.liverpool.liverpooltest.domain.model.Address;
import com.liverpool.liverpooltest.domain.model.User;

import java.util.List;

public record UserResponse(
        String id,
        String name,
        String paternalLastName,
        String maternalLastName,
        String email,
        AddressResponse address
) {
    public static UserResponse from(User user) {
        AddressResponse addressResponse = user.getAddress() != null
                ? AddressResponse.from(user.getAddress())
                : null;
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getPaternalLastName(),
                user.getMaternalLastName(),
                user.getEmail(),
                addressResponse
        );
    }

    public record AddressResponse(
            String postalCode,
            String municipality,
            String state,
            String city,
            List<String> neighborhoods,
            String country
    ) {
        public static AddressResponse from(Address address) {
            return new AddressResponse(
                    address.getPostalCode(),
                    address.getMunicipality(),
                    address.getState(),
                    address.getCity(),
                    address.getNeighborhoods(),
                    address.getCountry()
            );
        }
    }
}
