package org.kinocat.warehouses.dto;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class WarehouseFilter {
    private final Map<String, String[]> parameterMap;

    public WarehouseFilter(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }

    private StringBuilder parseWhere(String where, List<Object> parameters) {
        if (where.charAt(0) != '[' || where.charAt(where.length() - 1) != ']') throw new FilterException();
        String w = where.substring(1, where.length() - 1);

        String delimiter = "";
        String[] split = w.split(",");
        StringBuilder whereSb = new StringBuilder(" where ");
        for (String s : split) {
            String[] pair = s.split("=");

            if (pair.length == 2) {
                whereSb.append(delimiter).append(pair[0]).append(" = ?");
                parameters.add(pair[1]);
                delimiter = " and ";
            } else throw new FilterException();
        }
        return whereSb;
    }

    private String parseOrderBy(String sortBy) {
        StringJoiner joiner = new StringJoiner(",", " order by ", "");
        for (String s : sortBy.split(",")) {
            joiner.add(s);
        }
        return joiner.toString();
    }

    private int parseInt(String limit) {
        try {
            return Integer.parseInt(limit);
        } catch (NumberFormatException e) {
            throw new FilterException();
        }
    }

    public String buildSubQuery(List<Object> parameters) {
        StringBuilder sb = new StringBuilder();
        boolean mysqlLimit = false;

        if (parameterMap.containsKey("where")) {
            String whereValue = parameterMap.get("where")[0];
            sb.append(parseWhere(whereValue, parameters));
        }
        if (parameterMap.containsKey("orderby")) {
            String orderbyValue = parameterMap.get("orderby")[0];
            sb.append(parseOrderBy(orderbyValue));
        }
        if (parameterMap.containsKey("limit")) {
            String limitValue = parameterMap.get("limit")[0];
            sb.append(" limit ?");
            parameters.add(parseInt(limitValue));
            mysqlLimit = true;
        }
        if (parameterMap.containsKey("offset")) {
            String offsetValue = parameterMap.get("offset")[0];
            if (!mysqlLimit) {
                sb.append(" limit ?");
                parameters.add(Integer.MAX_VALUE); //MySql hack
            }
            sb.append(" offset ?");
            parameters.add(parseInt(offsetValue));
        }
        return sb.toString();
    }

    public static class FilterException extends RuntimeException {
        public FilterException() {
            super("Error parsing parameters");
        }
    }
}
