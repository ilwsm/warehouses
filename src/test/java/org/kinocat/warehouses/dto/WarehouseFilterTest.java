package org.kinocat.warehouses.dto;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class WarehouseFilterTest {

    /*
    where: [name=Main,country=usa]
    orderby: name,country
    limit: 2
    offset: 4
    */

    @Test
    void testBuildQuery() {

        assertDoesNotThrow(() -> {

            Map<String, String[]> map = new HashMap<>();
            map.put("where", new String[]{"[name=Main,country=usa]"});
            map.put("orderby", new String[]{"name,country"});
            map.put("limit", new String[]{"2"});
            map.put("offset", new String[]{"4"});
            WarehouseFilter filter = new WarehouseFilter(map);
            List<Object> parameters = new ArrayList<>();
            System.out.println("query = " + filter.buildSubQuery(parameters));

            for (int i = 0; i < parameters.size(); i++) {
                Object parameter = parameters.get(i);
                System.out.println((i + 1) + " - " + parameter);
            }
        });
    }
}