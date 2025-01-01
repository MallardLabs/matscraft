package com.mallardlabs.matscraft.ws;

import com.mallardlabs.matscraft.MatsCraft;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public final class WebSocketClientHandler extends WebSocketClient {

    private static volatile WebSocketClientHandler instance;
    private static final String DEFAULT_URI = "ws://localhost:8080"; // Renamed from URI to avoid conflict
    private boolean isReconnecting = false;
    private static final int MAX_RECONNECT_ATTEMPTS = 200;
    private int reconnectAttempts = 0;
    private static final long RECONNECT_DELAY = 5000; // 5 seconds delay between reconnects

    private MessageCallback messageCallback;

    private WebSocketClientHandler(URI serverUri) {
        super(serverUri);
    }

    /**
     * Initializes the WebSocket client with the given URI.
     * If a client is already connected, this method will skip reinitialization.
     *
     * @param uri The WebSocket server URI.
     */
    public static void initialize(String uri) {
        try {
            if (instance == null || !instance.isOpen()) {
                synchronized (WebSocketClientHandler.class) {
                    if (instance == null || !instance.isOpen()) {
                        URI serverUri = new URI(uri != null ? uri : DEFAULT_URI);
                        instance = new WebSocketClientHandler(serverUri);
                        instance.connect();
                        MatsCraft.LOGGER.info("WebSocket initialized and connecting to %s%n", serverUri);
                    }
                }
            }
        } catch (Exception e) {
            MatsCraft.LOGGER.error("Failed to initialize WebSocket: %s%n", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the current WebSocket client instance.
     *
     * @return The active WebSocket client instance, or null if not initialized.
     */
    public static WebSocketClientHandler getInstance() {
        return instance;
    }

    /**
     * Sends a message to the WebSocket server.
     *
     * @param message The message to send.
     */
    public static void sendMessage(String message) {
        WebSocketClientHandler client = WebSocketClientHandler.getInstance();
        if (client != null && client.isOpen()) {
            client.send(message);
            MatsCraft.LOGGER.info("Message sent: %s%n", message);
        } else {
            MatsCraft.LOGGER.warn("WebSocket is not open. Cannot send message.");

        }
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        MatsCraft.LOGGER.info("Connected to WebSocket server.");
        isReconnecting = false;
        reconnectAttempts = 0; // Reset reconnect attempts on successful connection
    }

    @Override
    public void onMessage(String message) {
        MatsCraft.LOGGER.info("Message from server: %s%n", message);
        if (messageCallback != null) {
            messageCallback.onMessage(message);
            messageCallback = null; // Reset callback after use
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        MatsCraft.LOGGER.warn("WebSocket connection closed (Code: %d, Reason: %s, Remote: %s)%n", code, reason, remote);
        if (!isReconnecting && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            new Thread(this::attemptReconnect).start(); // Start reconnection in new thread
        }
    }

    @Override
    public void onError(Exception ex) {
        MatsCraft.LOGGER.warn("WebSocket error: %s%n", ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * Handles reconnection attempts to the WebSocket server.
     */
    private void attemptReconnect() {
        isReconnecting = true;
        while (reconnectAttempts < MAX_RECONNECT_ATTEMPTS && !isOpen()) {
            reconnectAttempts++;
            MatsCraft.LOGGER.warn("Attempting to reconnect... (Attempt %d/%d)%n",
                            reconnectAttempts, MAX_RECONNECT_ATTEMPTS);
            try {
                instance.reconnect();
                Thread.sleep(RECONNECT_DELAY);
            } catch (Exception e) {
                MatsCraft.LOGGER.warn("Reconnect attempt %d failed: %s%n",
                                reconnectAttempts, e.getMessage());
            }
        }
        
        if (!isOpen()) {
            MatsCraft.LOGGER.warn("Failed to reconnect after maximum attempts.");
            isReconnecting = false;
        }
    }

    /**
     * Adds a method to manually reset the connection.
     */
    public static void resetConnection() {
        if (instance != null) {
            instance.close();
            instance = null;
        }
    }

    public static void sendPlayerJoin(String playerName, String uuid) {
        String message = String.format("{\"type\":\"player_join\",\"player\":\"%s\",\"uuid\":\"%s\"}", 
            playerName, uuid);
        if (instance != null && instance.isOpen()) {
            sendMessage(message);
        }
    }

    public void setMessageCallback(MessageCallback callback) {
        this.messageCallback = callback;
    }

    public interface MessageCallback {
        void onMessage(String message);
    }
}