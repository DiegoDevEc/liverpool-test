package com.liverpool.liverpooltest.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class User {
    private String id;
    private String name;
    private String paternalLastName;
    private String maternalLastName;
    private String email;
    private Address address;
}
