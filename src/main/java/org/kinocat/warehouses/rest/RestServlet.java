package org.kinocat.warehouses.rest;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.kinocat.warehouses.rest.exceptions.RestException;
import org.kinocat.warehouses.rest.handlers.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;

@Slf4j
@WebServlet(urlPatterns = "/rest/whs/*")
public class RestServlet extends HttpServlet {

    private RestApiGetHandler restApiGetHandler;
    private RestApiPostHandler restApiPostHandler;
    private RestApiPutHandler restApiPutHandler;
    private RestApiDeleteHandler restApiDeleteHandler;

    private static String getErrorResponse(Exception e) {
        return """
                {
                  "status_message": "%s",
                  "success": false
                }
                """.formatted(e.getMessage());
    }

    private static void setResponseContextType(HttpServletResponse resp) {
        resp.setContentType("application/json; charset=UTF-8");
    }

    @Override
    public void init() {
        this.restApiGetHandler = (RestApiGetHandler) getServletContext().getAttribute(RestApiGetHandler.class.getName());
        this.restApiPostHandler = (RestApiPostHandler) getServletContext().getAttribute(RestApiPostHandler.class.getName());
        this.restApiPutHandler = (RestApiPutHandler) getServletContext().getAttribute(RestApiPutHandler.class.getName());
        this.restApiDeleteHandler = (RestApiDeleteHandler) getServletContext().getAttribute(RestApiDeleteHandler.class.getName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGetOrDelete(req, resp, restApiGetHandler);
    }

    private void doGetOrDelete(HttpServletRequest req, HttpServletResponse resp, RestApiHandler handler) throws IOException {
        log.debug("Request {} arrived at URI: {} with query {}", req.getMethod(), req.getRequestURI(), req.getQueryString());
        setResponseContextType(resp);
        PrintWriter out = resp.getWriter();
        try {
            String responseStr = handler.handleRequest(req.getPathInfo(), req.getParameterMap(), null);
            out.write(responseStr);
        } catch (RestException e) {
            e.printStackTrace();
            resp.setStatus(e.getStatus());
            out.write(getErrorResponse(e));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPostOrPut(req, resp, restApiPostHandler);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPostOrPut(req, resp, restApiPutHandler);
    }

    private void doPostOrPut(HttpServletRequest req, HttpServletResponse resp, RestApiHandler handler) throws IOException {
        log.debug("Request {} arrived at URI: {} with query {}", req.getMethod(), req.getRequestURI(), req.getQueryString());
        String pathInfo = req.getPathInfo();
        setResponseContextType(resp);
        PrintWriter out = resp.getWriter();
        StringBuilder jb = new StringBuilder();
        String line;
        try {
            if (pathInfo != null && !pathInfo.equals("/")) throw new RestException("Method Not Allowed", SC_METHOD_NOT_ALLOWED);

            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);

            String responseStr = handler.handleRequest(pathInfo, req.getParameterMap(), jb.toString());
            out.write(responseStr);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (e instanceof RestException) {
                resp.setStatus(((RestException) e).getStatus());
            } else if (cause instanceof SQLException) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            out.write(getErrorResponse(e));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGetOrDelete(req, resp, restApiDeleteHandler);
    }
}