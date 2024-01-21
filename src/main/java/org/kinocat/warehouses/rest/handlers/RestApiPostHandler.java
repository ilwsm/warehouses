package org.kinocat.warehouses.rest.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.kinocat.warehouses.dao.WarehouseDao;
import org.kinocat.warehouses.entity.Warehouse;
import org.kinocat.warehouses.utils.Jackson;

import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
public class RestApiPostHandler implements RestApiHandler {
    private final ObjectMapper objectMapper = Jackson.objectMapper;
    private WarehouseDao warehouseDao;

    @Override
    public String handleRequest(String pathInfo, Map<String, String[]> parameterMap, String body) throws JsonProcessingException {
        Warehouse warehouse = objectMapper.readValue(body, Warehouse.class);
        Warehouse savedWarehouse = warehouseDao.save(warehouse);
        return objectMapper.writeValueAsString(savedWarehouse);
    }


}
