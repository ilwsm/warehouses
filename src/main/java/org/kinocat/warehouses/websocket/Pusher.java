package org.kinocat.warehouses.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint("/websocket/pusher")
public class Pusher {

    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    public static void sendAll(String text) {
        synchronized (SESSIONS) {
            for (Session session : SESSIONS) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(text);
                }
            }
        }
    }

    public static int getSessionCount() {
        return SESSIONS.size();
    }

    @OnOpen
    public void onOpen(Session session) {
        log.debug("Session with id {} opened", session.getId());
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        log.debug("Session with id {} closed", session.getId());
        SESSIONS.remove(session);
    }

    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msg, last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }
}
