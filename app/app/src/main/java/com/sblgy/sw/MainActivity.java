package com.sblgy.sw;

import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class MainActivity extends AppCompatActivity {
    private ImageButton buttonOpen;
    private ImageButton buttonClose;
    private Button buttonRes;
    private ImageView imageView;
    private String url = "http://192.168.1.8:5000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOpen = findViewById(R.id.button2);
        buttonClose = findViewById(R.id.button);
        buttonRes = findViewById(R.id.button3);
        imageView = findViewById(R.id.imageView);
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
                doPostRequest(url, 300);
            }
        });
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
                            if (responseData.contains("200")) {
                                textView.setText("已打开");
                                imageView.setImageResource(R.drawable.led0);
                            } else if (responseData.contains("400")) {
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
