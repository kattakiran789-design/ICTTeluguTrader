package com.icttelugu.trader;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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

    private LinearLayout chatContainer;
    private EditText inputMessage;
    private ScrollView scrollView;
    private OkHttpClient client;

    // మీ అసలైన Gemini API Key ని ఇక్కడ డబుల్ కోట్స్ మధ్యలో ఉంచండి
    private static final String GEMINI_API_KEY = "AQ.Ab8RN6L7d5arOGcB5JjTnImMBC6DTufOZ-BG_YF5SJ97wF5cRw ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new OkHttpClient();

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(20, 20, 20, 20);

        TextView header = new TextView(this);
        header.setText("📈 ICT AI Trader (Powered by Gemini)");
        header.setTextSize(20f);
        header.setPadding(0, 10, 0, 20);
        mainLayout.addView(header);

        scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        scrollView.setLayoutParams(scrollParams);

        chatContainer = new LinearLayout(this);
        chatContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(chatContainer);
        mainLayout.addView(scrollView);

        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);

        inputMessage = new EditText(this);
        inputMessage.setHint("ఉదా: BANKNIFTY 15m ICT Strategy...");
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        inputMessage.setLayoutParams(inputParams);

        Button sendButton = new Button(this);
        sendButton.setText("ASK AI");

        inputLayout.addView(inputMessage);
        inputLayout.addView(sendButton);
        mainLayout.addView(inputLayout);

        setContentView(mainLayout);

        addMessage("Bot", "నమస్తే! నేను మీ ICT AI Trader. ఏ ఇండెక్స్ లేదా స్టాక్ యొక్క ICT Analysis కావాలో టైప్ చేయండి.");

        sendButton.setOnClickListener(v -> {
            String query = inputMessage.getText().toString().trim();
            if (!query.isEmpty()) {
                addMessage("You", query);
                inputMessage.setText("");
                addMessage("Bot", "⏳ Gemini AI అనాలిసిస్ చేస్తోంది... దయచేసి వేచి ఉండండి.");
                getAiAnalysis(query);
            }
        });
    }

    private void addMessage(String sender, String message) {
        runOnUiThread(() -> {
            TextView textView = new TextView(MainActivity.this);
            textView.setText(sender + ":\n" + message + "\n");
            textView.setTextSize(15f);
            textView.setPadding(20, 20, 20, 20);

            if (sender.equals("You")) {
                textView.setBackgroundColor(0xFFE3F2FD);
            } else {
                textView.setBackgroundColor(0xFFF1F8E9);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 10, 0, 10);
            textView.setLayoutParams(params);

            chatContainer.addView(textView);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        });
    }

    private void getAiAnalysis(String userQuery) {
        String systemPrompt = "You are an expert Institutional ICT (Inner Circle Trader) Strategy Analyst. " +
                "Analyze: '" + userQuery + "'. " +
                "Provide detailed strategy breakdown including FVG, Order Flow, Entry, SL, and Target in simple Telugu.";

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;

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
                    addMessage("Bot", "❌ Connectivity Error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseData = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseData);
                            String aiReply = jsonResponse.getJSONArray("candidates")
                                    .getJSONObject(0)
                                    .getJSONObject("content")
                                    .getJSONArray("parts")
                                    .getJSONObject(0)
                                    .getString("text");

                            addMessage("Bot", aiReply);
                        } catch (Exception e) {
                            addMessage("Bot", "❌ Data Parsing Error: " + e.getMessage());
                        }
                    } else {
                        addMessage("Bot", "❌ API Error: దయచేసి API Key చెక్ చేసుకోండి.");
                    }
                }
            });

        } catch (Exception e) {
            addMessage("Bot", "❌ System Error: " + e.getMessage());
        }
    }
}
