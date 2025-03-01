package com.example.demo_fixerio.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ObjectGatewayResponse {
    private Integer timestamp;
    private String base;
    private Map<String, ?> rates;
}
