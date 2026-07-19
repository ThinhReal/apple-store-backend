package com.thinhreal.applestore.util;

import com.thinhreal.applestore.exception.BusinessException;

public final class ApiIdConverter {

    private ApiIdConverter() {
    }

    public static Long parseLongId(String id, String resource) {
        if (id == null || id.isBlank()) {
            throw new BusinessException("Missing " + resource + " id");
        }
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ex) {
            throw new BusinessException("Invalid " + resource + " id: " + id);
        }
    }

    public static String toApiId(Long id) {
        return id == null ? null : String.valueOf(id);
    }
}
