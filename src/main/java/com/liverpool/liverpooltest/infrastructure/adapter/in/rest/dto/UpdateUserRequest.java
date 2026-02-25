package com.liverpool.liverpooltest.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequest(
        String name,

        String paternalLastName,

        String maternalLastName,

        @Email(message = "Email format is not valid")
        String email,

        @Pattern(regexp = "\\d{5}", message = "Postal code must be 5 digits")
        String postalCode
) {}
