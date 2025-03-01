package com.example.demo_fixerio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingSearchResults<D> {
    private List<D> results = new ArrayList<>();
    private Integer offset;
    private Long total;
    private Integer limit;
}
