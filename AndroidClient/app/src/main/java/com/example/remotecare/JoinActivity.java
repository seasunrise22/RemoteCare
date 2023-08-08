package com.example.remotecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
    }

    public void onUserRegisterButtonClick(View view) {
        sendFormData();
    }

    public void sendFormData() {
        EditText userNameEditText = findViewById(R.id.joinUserName);
        String userName = userNameEditText.getText().toString();

        EditText userPasswordEditText = findViewById(R.id.joinUserPassword);
        String userPassword = userPasswordEditText.getText().toString();

        EditText userPasswordEditTextRepeat = findViewById(R.id.joinUserPasswordRepeat);
        String userPasswordRepeat = userPasswordEditTextRepeat.getText().toString();

        RadioGroup radioGroup = findViewById(R.id.joinRadioGroup);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        String userPosition = null;

        if(userName.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(userPassword.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(checkedId == R.id.radioButtonCaller) {
            userPosition = "caller";
        } else if(checkedId == R.id.radioButtonReceiver) {
            userPosition = "Receiver";
        } else {
            Toast.makeText(this, "발신자 또는 수신자를 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!userPassword.equals(userPasswordRepeat)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String postData = null;
        try {
            postData = "userName=" + URLEncoder.encode(userName, "UTF-8")
                    + "&userPassword=" + URLEncoder.encode(userPassword, "UTF-8")
                    + "&userPosition=" + URLEncoder.encode(userPosition, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString = "http://" + readIpAddress() + ":8081/auth/join";

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(postData, MediaType.parse("application/x-www-form-urlencoded"));
        Request request = new Request.Builder()
                .url(urlString)
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
                Log.d("Response", responseMessage);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(JoinActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                    }
                });
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