package org.kinocat.warehouses.rest.handlers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.kinocat.warehouses.dao.WarehouseDao;
import org.kinocat.warehouses.rest.exceptions.RestException;

import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
public class RestApiDeleteHandler implements RestApiHandler {
    private WarehouseDao warehouseDao;

    @Override
    public String handleRequest(String pathInfo, Map<String, String[]> parameterMap, String body) {
        try {
            int id = RestApiHandler.parseId(pathInfo);
            boolean result = warehouseDao.delete(id);
            if (!result) {
                throw new RestException("Failed to delete entity", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return "{\"result\":  true}\n";
        } catch (NumberFormatException e) {
            throw new RestException("Wrong Request", HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
