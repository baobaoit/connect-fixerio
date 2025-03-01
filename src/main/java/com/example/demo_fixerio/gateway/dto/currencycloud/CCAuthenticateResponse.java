package com.example.demo_fixerio.gateway.dto.currencycloud;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CCAuthenticateResponse {
    private String authToken;

    @JsonCreator
    public CCAuthenticateResponse(@JsonProperty("auth_token") String authToken) {
        this.authToken = authToken;
    }
}
