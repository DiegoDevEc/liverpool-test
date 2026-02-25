package com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CopomexInfo {
    @JsonProperty("asentamiento")
    private String settlement;
    @JsonProperty("tipo_asentamiento")
    private String settlementType;
    @JsonProperty("municipio")
    private String municipality;
    @JsonProperty("ciudad")
    private String city;
    @JsonProperty("estado")
    private String state;
    @JsonProperty("pais")
    private String country;
    @JsonProperty("codigo_postal")
    private String postalCode;
}
