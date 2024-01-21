package org.kinocat.warehouses.rest.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface RestApiHandler {
    String handleRequest(String pathInfo, Map<String, String[]> parameterMap, String body) throws JsonProcessingException;

    static int parseId(String pathInfo) {
        return Integer.parseInt(pathInfo.substring(1));
    }
}
