package com.example.remotecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginButtonClick(View view) {
        sendFormData();
    }

    public void onJoinButtonClick(View view) {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }

    public void sendFormData() {
        EditText userNameEditText = findViewById(R.id.loginUserName);
        String userName = userNameEditText.getText().toString();

        EditText userPasswordEditText = findViewById(R.id.loginUserPassword);
        String userPassword = userPasswordEditText.getText().toString();

        String postData = null;
        try {
            postData = "userName=" + URLEncoder.encode(userName, "UTF-8")
                    + "&userPassword=" + URLEncoder.encode(userPassword, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(postData, MediaType.parse("application/x-www-form-urlencoded"));
        Request request = new Request.Builder()
                .url("http://" + readIpAddress() +":8081/auth/login")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseMessage = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseMessage);
                    boolean success = json.getBoolean("success");
                    String message = json.getString("message");
                    Log.d("Response", responseMessage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    if(success) {
                        String token = json.getString("token");
                        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();;
                }
            }
        });
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