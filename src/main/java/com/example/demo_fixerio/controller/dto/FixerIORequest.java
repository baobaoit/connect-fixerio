package com.example.demo_fixerio.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Builder
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public final class FixerIORequest extends ObjectRequest {
    private Optional<String> dateFrom;
    private Optional<String> dateTo;
    private Optional<Integer> offset;
    private Optional<Integer> limit;

    @JsonCreator
    public FixerIORequest(@JsonProperty(value = "dateFrom") Optional<String> dateFrom,
                          @JsonProperty(value = "dateTo") Optional<String> dateTo,
                          @JsonProperty(value = "offset") Optional<Integer> offset,
                          @JsonProperty(value = "limit") Optional<Integer> limit) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.offset = offset;
        this.limit = limit;
    }
}
