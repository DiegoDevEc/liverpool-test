package com.liverpool.liverpooltest.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Paternal last name is required")
        String paternalLastName,

        String maternalLastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is not valid")
        String email,

        @NotBlank(message = "Postal code is required")
        @Pattern(regexp = "\\d{5}", message = "Postal code must be 5 digits")
        String postalCode
) {}
