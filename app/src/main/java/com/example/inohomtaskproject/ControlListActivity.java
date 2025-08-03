package com.example.inohomtaskproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ControlListActivity extends AppCompatActivity {

    private MyWebSocket myWebSocket;
    private String lightControlId = null; // Lamba kontrol ID'si tutulacak

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_list); // activity_control_list.xml

        ImageView btnLight = findViewById(R.id.btn_light); // XML'deki lamba ikonu yakalanıyor

        myWebSocket = new MyWebSocket(); // WebSocket bağlantısı
        myWebSocket.connect(); // Tekrar bağlan

        // Kontrol listesi verisi geldiğinde
        myWebSocket.setControlListListener(json -> runOnUiThread(() -> {
            try {
                JSONObject jsonObj = new JSONObject(json);
                JSONArray params = jsonObj.getJSONArray("params");
                JSONObject firstParam = params.getJSONObject(0);
                JSONArray dataArray = firstParam.getJSONArray("data");

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(i);
                    String name = obj.getString("name");
                    if ("Panel Lamba".equalsIgnoreCase(name)) {
                        lightControlId = obj.getString("id"); // Lamba ID'si alındı
                        Log.d("ControlListActivity", "Lamba ID: " + lightControlId);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }));

        // Kontrol listesi talep ediliyor
        myWebSocket.sendMessage("{\"is_request\":true,\"id\":5,\"params\":[{}],\"method\":\"GetControlList\"}");

        // Lamba ikonuna basılınca kontrol mesajı gönderilir
        btnLight.setOnClickListener(v -> {
            if (lightControlId != null) {
                String json = "{\"is_request\":true,\"id\":84,\"params\":[{\"id\":\"" + lightControlId + "\",\"value\":1}],\"method\":\"UpdateControlValue\"}";
                myWebSocket.sendMessage(json);
                Log.d("ControlListActivity", "Lamba güncelleme mesajı: " + json);
            } else {
                Log.d("ControlListActivity", "Lamba ID henüz alınmadı.");
            }
        });
    }
}
