package com.example.inohomtaskproject;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MyWebSocket extends WebSocketListener {

    private WebSocket webSocket;
    private static final String TAG = "MyWebSocket";

    // Giriş başarılı olduğunda tetiklenecek dinleyici
    public interface AuthListener {
        void onAuthenticated();
    }

    // Kontrol listesi geldiğinde tetiklenecek dinleyici
    public interface ControlListListener {
        void onControlListReceived(String json);
    }

    private AuthListener authListener;
    private ControlListListener controlListListener;

    public void setAuthListener(AuthListener listener) {
        this.authListener = listener;
    }

    public void setControlListListener(ControlListListener listener) {
        this.controlListListener = listener;
    }

    // WebSocket bağlantısı kurulur
    public void connect() {
        Log.d(TAG, "connect() çağrıldı");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("ws://85.105.107.53:9095") // ← Sunucu IP ve Port bilgisi
                .build();

        webSocket = client.newWebSocket(request, this);
    }
    // Giriş mesajı gönderilir
    public void sendLogin() {
        if (webSocket != null) {
            String json = "{\"is_request\":true,\"id\":8," +
                    "\"params\":[{\"username\":\"demo\",\"password\":\"123456\"}]," +
                    "\"method\":\"Authenticate\"}";
            webSocket.send(json);
            Log.d(TAG, "Login JSON gönderildi: " + json);
        }
    }

    // Genel mesaj gönderici (mesajı dışarıdan alır)
    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "WebSocket bağlantısı açıldı.");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Gelen mesaj: " + text);

        try {
            JSONObject json = new JSONObject(text);
            String method = json.optString("method");
            Log.d(TAG, "Method: " + method);


            if ("OnAuthenticated".equals(method)) {
                if (authListener != null) {
                    authListener.onAuthenticated();
                }
            } else if ("GetControlList".equals(method)) {
                if (controlListListener != null) {
                    controlListListener.onControlListReceived(text);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "WebSocket hata: ", t);
    }
     @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "WebSocket hata: ", t);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "WebSocket kapandı: " + code + " / " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "WebSocket kapandı: " + code + " / " + reason);
    }

}
