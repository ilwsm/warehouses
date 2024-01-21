package org.kinocat.warehouses.rest.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kinocat.warehouses.dao.WarehouseDao;
import org.kinocat.warehouses.entity.Warehouse;
import org.kinocat.warehouses.rest.exceptions.RestException;
import org.kinocat.warehouses.utils.Jackson;
import org.kinocat.warehouses.websocket.Pusher;

import java.util.Map;


@Slf4j
@Getter
@Setter
@AllArgsConstructor
public class RestApiPutHandler implements RestApiHandler {
    private final ObjectMapper objectMapper = Jackson.objectMapper;
    private WarehouseDao warehouseDao;

    @Override
    public String handleRequest(String pathInfo, Map<String, String[]> parameterMap, String body) throws JsonProcessingException {
        Warehouse warehouse = objectMapper.readValue(body, Warehouse.class);
        boolean result;

        int sessionCount = Pusher.getSessionCount();
        log.debug("Websocket sessionCount: " + sessionCount);

        if (sessionCount > 0) {
            // Не використововував в no production проекті тріггер засобами MySql,
            // тому що під кожну систему потрібно підбирати відповідну бібліотеку lib_mysqludf_sys і переносити в lib каталог MySql
            // Тому, поки такий варіант:
            Warehouse prevWarehouse = warehouseDao.findById(warehouse.getId());
            if (prevWarehouse == null) throw new RestException("Entity with this ID not found", HttpServletResponse.SC_NOT_FOUND);
            result = warehouseDao.update(warehouse);
            if (result) {
                int prevQuantity = prevWarehouse.getInventoryQuantity();
                int quantity = warehouse.getInventoryQuantity();
                if (prevQuantity != quantity) {
                    String text = "In the warehouse with ID %d, the amount of inventory has changed from %d to %d"
                            .formatted(warehouse.getId(), prevQuantity, quantity);
                    Pusher.sendAll(text);
                    log.debug("Clients notified: " + text);
                }
            }
        } else {
            result = warehouseDao.update(warehouse);
            if (!result) {
                if (warehouseDao.findById(warehouse.getId()) == null)
                    throw new RestException("Entity with this ID not found", HttpServletResponse.SC_NOT_FOUND);
            }
        }

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put("success", result);
        return objectMapper.writeValueAsString(rootNode);
    }
}
