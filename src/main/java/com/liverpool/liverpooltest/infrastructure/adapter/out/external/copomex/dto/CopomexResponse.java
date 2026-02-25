package com.liverpool.liverpooltest.infrastructure.adapter.out.external.copomex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CopomexResponse {
    private boolean error;
    @JsonProperty("code_error")
    private int codeError;
    @JsonProperty("error_message")
    private String errorMessage;
    private CopomexInfo response;
}
