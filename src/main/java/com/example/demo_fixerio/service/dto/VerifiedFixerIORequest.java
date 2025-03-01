package com.example.demo_fixerio.service.dto;

import com.example.demo_fixerio.controller.dto.ObjectRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class VerifiedFixerIORequest extends ObjectRequest {
    private String dateFrom;
    private String dateTo;
    private int offset;
    private int limit;
}
