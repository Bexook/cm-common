package com.cm.common.util;

public class SqlUtils {

    public static String wrapToSqlLikeStatement(final String value) {
        return "%" + value + "%";
    }

}
