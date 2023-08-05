package com.example.remotecare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private CameraManager cameraManager;
    private TextureView callerTextureView;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callerTextureView = findViewById(R.id.callerCameraView);

        showUserOnToolbar();
    }

    public void showUserOnToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d(TAG, "showUserOnToolbar: " + token);

        if (token != null) {
            try {
                String[] splitToken = token.split("\\.");
                String base64EncodedBody = splitToken[1];
                String body = new String(Base64.decode(base64EncodedBody, Base64.DEFAULT));
                JSONObject json = new JSONObject(body);
                String userId = json.getString("userId");

                TextView usernameToolbarTextView = findViewById(R.id.usernameToolbarTextView);
                usernameToolbarTextView.setText(userId + " 님 환영합니다.");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCallButtonClick(View view) {
        Log.d(TAG, "onCallButtonClick: clicked");
        requestCameraPermission();
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requestCameraPermission: if");
            // 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // 이미 권한이 허용된 상태이면, 카메라 기능을 사용할 수 있습니다.
            // 이곳에 화상통화를 시작하는 코드를 추가하세요.
            Log.d(TAG, "requestCameraPermission: else");
            startVideoCall();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었으므로, 카메라 기능을 사용할 수 있습니다.
                // 이곳에 화상통화를 시작하는 코드를 추가하세요.
                startVideoCall();
            } else {
                // 권한이 거부되었으므로, 카메라 기능을 사용할 수 없습니다.
                // 사용자에게 권한이 필요하다는 메시지를 보여줄 수 있습니다.
                Toast.makeText(this, "화상통화를 위해서 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startVideoCall() {
        Log.d(TAG, "startVideoCall: enter");
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            Log.d(TAG, "startVideoCall: try1");
            String cameraId = cameraManager.getCameraIdList()[1];
            try {
                Log.d(TAG, "startVideoCall: try2");
                cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice cameraDevice) {
                        // 카메라가 성공적으로 열린 경우
                        // 미리보기를 TextureView에 연결하고 시작
                        try {
                            Log.d(TAG, "startVideoCall: onOpened");
                            SurfaceTexture surfaceTexture = callerTextureView.getSurfaceTexture();
                            if (surfaceTexture != null) {
                                Surface surface = new Surface(surfaceTexture);
                                OutputConfiguration outputConfig = new OutputConfiguration(surface);
                                Executor executor = ContextCompat.getMainExecutor(MainActivity.this);
                                SessionConfiguration config = new SessionConfiguration(
                                        SessionConfiguration.SESSION_REGULAR,
                                        Collections.singletonList(outputConfig),
                                        executor,
                                        new CameraCaptureSession.StateCallback() {
                                            @Override
                                            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                                // 카메라 세션이 성공적으로 설정된 경우
                                                // 미리보기 시작
                                                try {
                                                    CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                                    captureRequestBuilder.addTarget(surface);
                                                    CaptureRequest captureRequest = captureRequestBuilder.build();
                                                    cameraCaptureSession.setRepeatingRequest(captureRequest, null, null);
                                                } catch (CameraAccessException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                                            }
                                        });
                                cameraDevice.createCaptureSession(config);
                            }
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice cameraDevice) {

                    }

                    @Override
                    public void onError(@NonNull CameraDevice cameraDevice, int i) {

                    }
                }, null);
            } catch (SecurityException e) {
                Log.d(TAG, "SecurityException: try cameraManager.openCamera");
                e.printStackTrace();
                Toast.makeText(this, "화상통화를 위해서 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            ;
        }
        return ipAddress;
    }
}