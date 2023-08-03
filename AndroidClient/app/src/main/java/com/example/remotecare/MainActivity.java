package com.example.remotecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showUserOnToolbar();

        Button callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserOnToolbar();
            }
        });
    }

    public void showUserOnToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d(TAG, "showUserOnToolbar: " + token);

        if(token != null) {
            try {
                String[] splitToken = token.split("\\.");
                String base64EncodedBody = splitToken[1];
                String body = new String(Base64.decode(base64EncodedBody, Base64.DEFAULT));
                JSONObject json = new JSONObject(body);
                String userId = json.getString("userId");

                TextView usernameToolbarTextView = findViewById(R.id.usernameToolbarTextView);
                usernameToolbarTextView.setText(userId + " 님 환영합니다.");
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String readIpAddress() {
        String ipAddress = "";
        try {
            InputStream inputStream = getAssets().open("serverIp.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            ipAddress = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();;
        }
        return ipAddress;
    }
}