package com.example.inohomtaskproject;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MyWebSocket extends WebSocketListener {

    private WebSocket webSocket;
    private static final String TAG = "MyWebSocket";

    // Giriş başarılı olursa MainActivity'ye haber vermek için interface tanımı
    public interface AuthListener {
        void onAuthenticated(); // Başarılı login olduğunda çağrılacak
    }

    private AuthListener authListener;

    public void setAuthListener(AuthListener listener) {
        this.authListener = listener;
    }

    // WebSocket bağlantısını başlat
    public void connect() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://85.105.107.53:9095") // Ödevde verilen IP ve port
                .build();

        // Bu sınıf WebSocketListener olduğu için kendisini bağlayabiliyoruz
        webSocket = client.newWebSocket(request, this);
    }

    // Giriş JSON’unu sunucuya gönderen metod
    public void sendLogin(String username, String password) {
        if (webSocket != null) {
            String json = "{\"is_request\":true,\"id\":8,\"params\":[{\"username\":\"" +
                    username + "\",\"password\":\"" + password + "\"}],\"method\":\"Authenticate\"}";
            webSocket.send(json);
            Log.d(TAG, "Login mesajı gönderildi: " + json);
        } else {
            Log.e(TAG, "WebSocket bağlantısı yok, mesaj gönderilemedi.");
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "WebSocket bağlantısı kuruldu.");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Gelen mesaj: " + text);

        // Eğer "OnAuthenticated" mesajı geldiyse giriş başarılıdır
        if (text.contains("\"method\":\"OnAuthenticated\"") && text.contains("\"error\":null")) {
            if (authListener != null) {
                authListener.onAuthenticated(); // Activity’ye haber ver
            }
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "WebSocket hatası: " + t.getMessage());
    }
}
