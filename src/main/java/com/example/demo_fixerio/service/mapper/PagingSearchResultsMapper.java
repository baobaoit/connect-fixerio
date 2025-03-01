package com.example.demo_fixerio.service.mapper;

import com.example.demo_fixerio.controller.dto.ObjectRequest;
import com.example.demo_fixerio.domain.model.PagingSearchResults;
import com.example.demo_fixerio.service.dto.VerifiedFixerIORequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public final class PagingSearchResultsMapper {
    public <T> List<T> toPagingResults(List<T> list, ObjectRequest request) {
        if (CollectionUtils.isNotEmpty(list) && Objects.nonNull(request) && request instanceof VerifiedFixerIORequest) {
            VerifiedFixerIORequest objRequest = (VerifiedFixerIORequest) request;
            int fromIndex = Math.min(objRequest.getOffset(), list.size());
            int toIndex = Math.min(objRequest.getOffset() + objRequest.getLimit(), list.size());
            return list.subList(fromIndex, toIndex);
        }
        return new ArrayList<>();
    }

    public <T> PagingSearchResults<T> toPaging(List<T> list, ObjectRequest request) {
        PagingSearchResults<T> results = new PagingSearchResults<>();
        if (CollectionUtils.isNotEmpty(list) && Objects.nonNull(request) && request instanceof VerifiedFixerIORequest) {
            VerifiedFixerIORequest verifiedRequest = (VerifiedFixerIORequest) request;
            results.setResults(toPagingResults(list, request));
            results.setTotal((long) list.size());
            results.setOffset(verifiedRequest.getOffset());
            results.setLimit(verifiedRequest.getLimit());
        }
        return results;
    }
}
