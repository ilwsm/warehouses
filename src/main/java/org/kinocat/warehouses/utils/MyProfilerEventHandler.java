package org.kinocat.warehouses.utils;

import com.mysql.cj.Query;
import com.mysql.cj.Session;
import com.mysql.cj.log.ProfilerEvent;
import com.mysql.cj.protocol.Resultset;

public class MyProfilerEventHandler extends com.mysql.cj.log.LoggingProfilerEventHandler{

    @Override
    public void processEvent(byte eventType, Session session, Query query, Resultset resultSet, long eventDuration, Throwable eventCreationPoint, String message) {
        if (eventType != ProfilerEvent.TYPE_QUERY) return;
        super.processEvent(eventType, session, query, resultSet, eventDuration, eventCreationPoint, message);
    }
}
