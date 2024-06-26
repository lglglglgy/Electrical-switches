package com.sblgy.sw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {
    private ImageButton buttonOpen;
    private ImageButton buttonClose;
    private Button buttonRes;
    private ImageView imageView;

    private Button button4;
   private String url = "http://8.130.125.247:5000/";


    private int clickCount = 0;
    private long startTime = 0;
    private final int MAX_CLICKS = 5;
    private final long MAX_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOpen = findViewById(R.id.button2);
        buttonClose = findViewById(R.id.button);
        buttonRes = findViewById(R.id.button3);
        imageView = findViewById(R.id.imageView);
        button4 = findViewById(R.id.button4);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String ipAddress = sharedPreferences.getString("ip_address", "8.130.125.247");
        url = "http://" + ipAddress +":5000";

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPostRequest(url, 200);
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPostRequest(url, 400);
            }
        });

        buttonRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = sharedPreferences.getString("ip_address", "8.130.125.247");
                url = "http://" + ipAddress +":5000";
                Log.d("MainActivity", "IP Address: " + ipAddress);
                Toast.makeText(MainActivity.this, "当前服务器的ip为：" + ipAddress, Toast.LENGTH_SHORT).show();

            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick();

            }
        });
    }
    private void handleButtonClick() {
        long currentTime = System.currentTimeMillis();

        if (startTime == 0 || currentTime - startTime > MAX_DURATION) {
            startTime = currentTime;
            clickCount = 0;
        }

        clickCount++;

        if (clickCount == MAX_CLICKS) {
            startTime = 0;
            clickCount = 0;
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    }


    private void doPostRequest(String url, int code) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS) // set timeout to 1 second
                .writeTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonData = "{\"code\": " + code + "}";
        RequestBody body = RequestBody.create(JSON, jsonData);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Display a dialog to the user
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage("Request timed out")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // do something with the response
                    String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Assuming you have a TextView with id "textView" to display the response
                            TextView textView = findViewById(R.id.textView);
                            if (responseData.contains("400")) {
                                textView.setText("已打开");
                                imageView.setImageResource(R.drawable.led0);
                            } else if (responseData.contains("200")) {
                                textView.setText("已关闭");
                                imageView.setImageResource(R.drawable.led1);
                            } else if (responseData.contains("300")) {
                                textView.setText("已重置");
                                imageView.setImageResource(R.drawable.reset);
                            } else {
                                textView.setText("服务器访问失败");
                                imageView.setImageResource(R.drawable.technology);
                            }
                        }
                    });

                }
            }
        });
    }
}
