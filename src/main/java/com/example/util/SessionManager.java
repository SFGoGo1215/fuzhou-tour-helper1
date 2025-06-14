package com.example.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final Map<String, String> sessionHistories = new ConcurrentHashMap<>();

    public String getHistory(String sessionId) {
        return sessionHistories.getOrDefault(sessionId, "");
    }

    public void saveHistory(String sessionId, String history) {
        sessionHistories.put(sessionId, history);
    }

    public void clearHistory(String sessionId) {
        sessionHistories.remove(sessionId);
    }
}