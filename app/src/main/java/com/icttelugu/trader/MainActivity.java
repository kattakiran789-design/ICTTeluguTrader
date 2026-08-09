package com.icttelugu.trader;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText inputMessage;
    private Button btnSend;
    private ListView chatListView;
    private ArrayList<String> chatList;
    private ArrayAdapter<String> chatAdapter;

    private OkHttpClient client;
    private static final String GROQ_API_KEY = "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"; // మీ Groq API Key ని ఇక్కడ పేస్ట్ చేయండి

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        chatListView = findViewById(R.id.chatListView);

        chatList = new ArrayList<>();
        chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatList);
        chatListView.setAdapter(chatAdapter);

        client = new OkHttpClient();

        // ప్రారంభ సందేశం (Welcome Message)
        addMessage("Bot", "Namaste! Nenu mi ICT AI Trader. E index leda stock yokka ICT Analysis kavalo type cheyandi.");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userQuery = inputMessage.getText().toString().trim();
                if (!userQuery.isEmpty()) {
                    addMessage("You", userQuery);
                    inputMessage.setText("");
                    addMessage("Bot", "AI analysis chestondi... Dayachesi vechi undandi.");
                    getAiAnalysis(userQuery);
                }
            }
        });
    }

    private void addMessage(String sender, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatList.add(sender + ":\n\n" + message);
                chatAdapter.notifyDataSetChanged();
                chatListView.setSelection(chatList.size() - 1);
            }
        });
    }

    private void getAiAnalysis(String userQuery) {
        String systemPrompt = "You are a professional ICT (Inner Circle Trader) Strategy Analyst for Indian Markets (Nifty, BankNifty, Stocks, Sensex).\n" +
                "The user asked about: '" + userQuery + "'.\n\n" +
                "Analyze the market strictly following ICT Order Block & Liquidity concepts. Output ONLY in clear TELUGU mixed with English technical terms.\n\n" +
                "Provide the response strictly in this format:\n\n" +
                "📊 **" + userQuery.toUpperCase() + " ICT & ORDER BLOCK ANALYSIS**\n\n" +
                "1. **Market Structure & Liquidity Sweep:**\n" +
                "   - Market Trend / Bias (Bullish / Bearish)\n" +
                "   - Liquidity Sweep (Buy-side / Sell-side Liquidity taken)\n\n" +
                "2. **Order Block (OB) Identification:**\n" +
                "   - **Bullish Order Block Zone:** (Key demand/buying OB level)\n" +
                "   - **Bearish Order Block Zone:** (Key supply/selling OB level)\n" +
                "   - **Order Block Status:** (Mitigated or Unmitigated)\n\n" +
                "3. **Fair Value Gap (FVG):**\n" +
                "   - Important Imbalance / FVG range\n\n" +
                "4. **Exact Trade Execution Plan:**\n" +
                "   - **Entry Point:** (Exact level near Order Block / FVG)\n" +
                "   - **Stop Loss (SL):** (Strict level below/above OB)\n" +
                "   - **Target (TP1 & TP2):** (Next Liquidity Pool / High / Low)\n" +
                "   - **Risk-to-Reward Ratio:** (e.g., 1:2.5)\n\n" +
                "5. **Confirmation for Entry:**\n" +
                "   - 5-Min Market Structure Shift (MSS / Choch) level needed for trigger.\n\n" +
                "Do NOT give generic definitions. Provide precise trading levels, Order Block zones, and strategy execution steps.";

        String url = "https://api.groq.com/openai/v1/chat/completions";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.3-70b-versatile");
            jsonBody.put("temperature", 0.3);

            JSONArray messages = new JSONArray();
            
            JSONObject systemObj = new JSONObject();
            systemObj.put("role", "system");
            systemObj.put("content", systemPrompt);
            messages.put(systemObj);

            JSONObject userObj = new JSONObject();
            userObj.put("role", "user");
            userObj.put("content", userQuery);
            messages.put(userObj);

            jsonBody.put("messages", messages);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonBody.toString()
            );

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + GROQ_API_KEY)
                    .addHeader("Content-Type", "application/json")
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
                            String aiReply = jsonResponse.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

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
