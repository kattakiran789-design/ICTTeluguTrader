package com.icttelugu.trader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.icttelugu.trader.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // మీ Google AI Studio API Key ని ఇక్కడ డబుల్ కోట్స్ మధ్యలో పేస్ట్ చేయండి
    private static final String GEMINI_API_KEY = "AQ.Ab8RN6KFug6rMPHmCJseGJBY2zOgAsRtJcZW9cwbq7C5PskDfw";

    private EditText queryInput;
    private Button askButton;
    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private OkHttpClient client;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryInput = findViewById(R.id.queryInput);
        askButton = findViewById(R.id.askButton);
        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);

        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());

        addMessage("Bot", "Namaste! Nenu mi ICT AI Trader. E index leda stock yokka ICT Analysis kavalo type cheyandi.");

        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userQuery = queryInput.getText().toString().trim();
                if (!userQuery.isEmpty()) {
                    addMessage("You", userQuery);
                    queryInput.setText("");
                    addMessage("Bot", "Gemini AI analysis chestondi... Dayachesi vechi undandi.");
                    getAiAnalysis(userQuery);
                }
            }
        });
    }

    private void addMessage(String sender, String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView textView = new TextView(MainActivity.this);
                textView.setText(sender + ":\n" + message);
                textView.setTextSize(16);
                textView.setPadding(20, 20, 20, 20);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 10, 0, 10);
                textView.setLayoutParams(params);

                if (sender.equals("You")) {
                    textView.setBackgroundColor(0xFFE3F2FD);
                } else {
                    textView.setBackgroundColor(0xFFF1F8E9);
                }

                chatContainer.addView(textView);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    private void getAiAnalysis(String userQuery) {
        String systemPrompt = "You are an expert Institutional ICT Strategy Analyst. " +
                "Analyze: '" + userQuery + "'. " +
                "Provide detailed strategy breakdown including FVG, Order Flow, Entry, SL, and Target in simple Telugu.";

        // Updated Endpoint using active model: gemini-2.0-flash
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;

        try {
            JSONObject jsonBody = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject contentObj = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject partObj = new JSONObject();

            partObj.put("text", systemPrompt);
            parts.put(partObj);
            contentObj.put("parts", parts);
            contents.put(contentObj);
            jsonBody.put("contents", contents);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonBody.toString()
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    addMessage("Bot", "Connection Failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body() != null ? response.body().string() : "";

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String aiReply = jsonResponse.getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");

                            addMessage("Bot", aiReply);
                        } catch (Exception e) {
                            addMessage("Bot", "Data Format Issue: " + e.getMessage());
                        }
                    } else {
                        addMessage("Bot", "Server Status Code " + response.code() + ": " + responseData);
                    }
                }
            });

        } catch (Exception e) {
            addMessage("Bot", "App Logic Issue: " + e.getMessage());
        }
    }
}
