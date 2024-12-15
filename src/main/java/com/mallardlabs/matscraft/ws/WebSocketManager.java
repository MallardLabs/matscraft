package com.mallardlabs.matscraft.ws;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class WebSocketManager {

    private static WebSocketClient webSocketClient;
    private static final int RECONNECT_DELAY_MS = 5000; // 5 seconds delay for reconnection
    private static final Gson gson = new Gson();
    private static boolean isInitialized = false;

    private static Consumer<String> onMessageCallback; // Callback to handle messages

    public static void initializeWebSocket(String websocketUrl) {
        if (isInitialized && webSocketClient != null && webSocketClient.isOpen()) {
            return; // Avoid re-initializing if already connected
        }

        try {
            URI serverUri = new URI(websocketUrl);
            webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("WebSocket connected.");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received message: " + message);
                    // Invoke the callback to send the response back to the player
                    if (onMessageCallback != null) {
                        onMessageCallback.accept(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.err.println("WebSocket closed: " + reason);
                    scheduleReconnect(websocketUrl);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("WebSocket error: " + ex.getMessage());
                    scheduleReconnect(websocketUrl);
                }
            };

            webSocketClient.connect();
            isInitialized = true;

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void scheduleReconnect(String websocketUrl) {
        System.out.println("Scheduling WebSocket reconnection...");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Attempting to reconnect WebSocket...");
                initializeWebSocket(websocketUrl);
            }
        }, RECONNECT_DELAY_MS);
    }

    public static void sendMessage(String event, Object data, Consumer<String> messageCallback) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            String jsonPayload = gson.toJson(new WebSocketMessage(event, data));
            webSocketClient.send(jsonPayload);
            System.out.println("Message sent: " + jsonPayload);

            // Set the callback to handle the response
            onMessageCallback = messageCallback;
        } else {
            System.err.println("WebSocket is not connected. Message not sent.");
        }
    }

    // Nested class to structure WebSocket messages
    private static class WebSocketMessage {
        private final String event;
        private final Object data;

        public WebSocketMessage(String event, Object data) {
            this.event = event;
            this.data = data;
        }
    }
}