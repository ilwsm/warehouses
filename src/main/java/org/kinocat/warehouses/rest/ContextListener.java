package org.kinocat.warehouses.rest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.kinocat.warehouses.dao.WarehouseDao;
import org.kinocat.warehouses.rest.handlers.RestApiDeleteHandler;
import org.kinocat.warehouses.rest.handlers.RestApiGetHandler;
import org.kinocat.warehouses.rest.handlers.RestApiPostHandler;
import org.kinocat.warehouses.rest.handlers.RestApiPutHandler;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();

        WarehouseDao warehouseDao = new WarehouseDao();
        warehouseDao.createTable();

        servletContext.setAttribute(WarehouseDao.class.getName(), warehouseDao);
        servletContext.setAttribute(RestApiGetHandler.class.getName(), new RestApiGetHandler(warehouseDao));
        servletContext.setAttribute(RestApiPostHandler.class.getName(), new RestApiPostHandler(warehouseDao));
        servletContext.setAttribute(RestApiPutHandler.class.getName(), new RestApiPutHandler(warehouseDao));
        servletContext.setAttribute(RestApiDeleteHandler.class.getName(), new RestApiDeleteHandler(warehouseDao));

    }
}
