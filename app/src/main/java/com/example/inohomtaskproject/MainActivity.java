package com.example.inohomtaskproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MyWebSocket myWebSocket;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // activity_main.xml yüklendi

        Button loginButton = findViewById(R.id.button); // XML'deki buton yakalanıyor

        myWebSocket = new MyWebSocket(); // WebSocket nesnesi oluşturuluyor
        myWebSocket.connect(); // WebSocket bağlantısı başlatılıyor

        // Giriş başarılı olursa sayfa geçişi yapılır
        myWebSocket.setAuthListener(() -> {
            Log.d(TAG, "Giriş başarılı, diğer sayfaya geçiliyor.");
            Intent intent = new Intent(MainActivity.this, ControlListActivity.class);
            startActivity(intent);
        });

        // Butona tıklanınca giriş mesajı gönder
        loginButton.setOnClickListener(v -> {
            myWebSocket.sendLogin(); // demo kullanıcı adı/şifre gönder
        });
    }
}
