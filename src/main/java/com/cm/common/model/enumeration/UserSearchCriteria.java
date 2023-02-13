package com.cm.common.model.enumeration;

import com.cm.common.classifiers.SearchCriteria;

public enum UserSearchCriteria implements SearchCriteria {
    BY_EMAIL_VERIFIED,
    BY_ACTIVE,
    BY_USER_ROLE,
    BY_EMAIL,
    BY_LASTNAME,
    BY_FIRSTNAME
}