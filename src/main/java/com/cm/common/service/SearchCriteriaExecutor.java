package com.cm.common.service;

import java.util.Set;


@FunctionalInterface
public interface SearchCriteriaExecutor<T> {
    Set<T> search(final Object searchKey);

}
