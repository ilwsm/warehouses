package org.kinocat.warehouses.rest.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.kinocat.warehouses.dao.WarehouseDao;
import org.kinocat.warehouses.dto.WarehouseFilter;
import org.kinocat.warehouses.entity.Warehouse;
import org.kinocat.warehouses.rest.exceptions.RestException;
import org.kinocat.warehouses.utils.Jackson;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
public class RestApiGetHandler implements RestApiHandler {
    private final ObjectMapper objectMapper = Jackson.objectMapper;
    private WarehouseDao warehouseDao;


    @Override
    public String handleRequest(String pathInfo, Map<String, String[]> parameterMap, String body) throws JsonProcessingException {
        if (pathInfo == null || pathInfo.equals("/")) {

            List<Warehouse> allTasks = parameterMap.isEmpty() ? warehouseDao.findAll() : findAllWithFilters(parameterMap);

            ObjectNode rootNode = objectMapper.createObjectNode();
            rootNode.put("success", true);
            rootNode.put("total_results", allTasks.size());
            ArrayNode jsonNode = objectMapper.valueToTree(allTasks);
            rootNode.set("results", jsonNode);

            return objectMapper.writeValueAsString(rootNode);
        }
        try {
            int id = RestApiHandler.parseId(pathInfo);
            Warehouse warehouse = warehouseDao.findById(id);
            if (warehouse == null) throw new RestException("Entity with this ID not found", HttpServletResponse.SC_NOT_FOUND);
            return objectMapper.writeValueAsString(warehouse);
        } catch (NumberFormatException e) {
            throw new RestException("Wrong Request", HttpServletResponse.SC_NOT_FOUND);
        }

    }

    private List<Warehouse> findAllWithFilters(Map<String, String[]> parameterMap) {
        try {
            WarehouseFilter filter = new WarehouseFilter(parameterMap);
            return warehouseDao.findAll(filter);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();

            if (cause instanceof java.sql.SQLSyntaxErrorException || e instanceof WarehouseFilter.FilterException) {
                throw new RestException(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else throw e;
        }
    }
}
