package com.mallardlabs.matscraft.config;

public class ConfigManager {
    private static String baseURL = "http://localhost:3000";
    private static String websocketUrl = "ws://localhost:3000"; // Default URL

    public static String getBaseURL() {
        return baseURL;
    }
    public static String getWebSocketUrl() {
        return websocketUrl;
    }

}