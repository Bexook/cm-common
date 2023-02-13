package com.cm.common.classifiers;

import com.cm.common.service.SearchCriteriaExecutor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface Searchable {

    default <T> Set<T> searchByCriteria(final Map<? extends SearchCriteria, Object> criteria, final Map<SearchCriteria, SearchCriteriaExecutor<T>> searchers) {
        return criteria.entrySet().stream()
                .map(c -> searchers.get(c.getKey())
                        .search(c.getValue()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
