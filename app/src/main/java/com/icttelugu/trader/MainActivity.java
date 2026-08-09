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

        addMessage("Bot", "Namaste! Nenu mi ICT AI Trader. Symbol type cheyandi (e.g., RELIANCE.NS, TCS.NS, ^NSEI for Nifty).");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userQuery = inputMessage.getText().toString().trim();
                if (!userQuery.isEmpty()) {
                    addMessage("You", userQuery);
                    inputMessage.setText("");
                    addMessage("Bot", "Yahoo Finance nundi Live Market Data teeskuntondi...");
                    fetchLiveMarketData(userQuery);
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

    // Step 1: Yahoo Finance API నుండి Live Market Data పొందే పద్ధతి
    private void fetchLiveMarketData(String symbol) {
        String formattedSymbol = symbol.toUpperCase().trim();
        
        // Indian Stocks కోసం .NS suffix లేకపోతే ఆటోమేటిక్‌గా యాడ్ చేస్తుంది
        if (!formattedSymbol.contains(".") && !formattedSymbol.startsWith("^")) {
            formattedSymbol = formattedSymbol + ".NS";
        }

        String yahooUrl = "https://query1.finance.yahoo.com/v8/finance/chart/" + formattedSymbol + "?interval=1m&range=1d";

        Request request = new Request.Builder()
                .url(yahooUrl)
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();

        String finalSymbol = formattedSymbol;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                addMessage("Bot", "Yahoo Finance Data Fetch Failed: " + e.getMessage() + "\nFallback analysis chestondi...");
                getAiAnalysis(finalSymbol, 0, 0, 0, 0, false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject result = jsonObject.getJSONObject("chart").getJSONArray("result").getJSONObject(0);
                        JSONObject meta = result.getJSONObject("meta");

                        double ltp = meta.optDouble("regularMarketPrice", 0.0);
                        double dayHigh = meta.optDouble("regularMarketDayHigh", 0.0);
                        double dayLow = meta.optDouble("regularMarketDayLow", 0.0);
                        double previousClose = meta.optDouble("chartPreviousClose", 0.0);

                        addMessage("Bot", "Live Data Received! Current Price: ₹" + ltp + "\nAI Intraday Analysis chestondi...");
                        getAiAnalysis(finalSymbol, ltp, dayHigh, dayLow, previousClose, true);

                    } catch (Exception e) {
                        getAiAnalysis(finalSymbol, 0, 0, 0, 0, false);
                    }
                } else {
                    getAiAnalysis(finalSymbol, 0, 0, 0, 0, false);
                }
            }
        });
    }

    // Step 2: Live Data ని Prompt లోకి పంపి AI విశ్లేషణ పొందడం
    private void getAiAnalysis(String symbol, double ltp, double dayHigh, double dayLow, double prevClose, boolean hasLiveData) {
        
        String liveContext = "";
        if (hasLiveData) {
            liveContext = "REAL-TIME MARKET DATA FOR " + symbol + ":\n" +
                    "- Current Price (LTP): " + ltp + "\n" +
                    "- Today High: " + dayHigh + "\n" +
                    "- Today Low: " + dayLow + "\n" +
                    "- Previous Close: " + prevClose + "\n\n" +
                    "STRICT INSTRUCTION: Calculate exact Order Blocks, FVG, Entry, Stop Loss, and Targets relative to the provided Current Price (" + ltp + "). Do NOT invent random prices.";
        } else {
            liveContext = "Live market data unavailable for " + symbol + ". Provide structural ICT Order Block analysis.";
        }

        String systemPrompt = "You are a professional ICT Strategy Analyst for Indian Markets.\n" + liveContext + "\n\n" +
                "Output ONLY in clear TELUGU mixed with English technical terms in this format:\n\n" +
                "📊 **" + symbol.toUpperCase() + " REAL-TIME ICT ANALYSIS**\n\n" +
                "1. **Market Structure & Liquidity Sweep:**\n" +
                "   - Market Bias (Bullish / Bearish)\n" +
                "   - Liquidity Sweep Level\n\n" +
                "2. **Order Block (OB) Identification:**\n" +
                "   - **Bullish Order Block Zone:** (Demand OB near LTP)\n" +
                "   - **Bearish Order Block Zone:** (Supply OB near LTP)\n" +
                "   - **Status:** (Mitigated / Unmitigated)\n\n" +
                "3. **Fair Value Gap (FVG):**\n" +
                "   - Imbalance / FVG range\n\n" +
                "4. **Exact Intraday Execution Plan:**\n" +
                "   - **Entry Point:** (Exact level near current LTP/OB)\n" +
                "   - **Stop Loss (SL):** (Strict level)\n" +
                "   - **Target (TP1 & TP2):** (Target levels)\n" +
                "   - **Risk-to-Reward Ratio:**\n\n" +
                "5. **Confirmation:**\n" +
                "   - 5-Min MSS / Choch level needed.";

        String url = "https://api.groq.com/openai/v1/chat/completions";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.3-70b-versatile");
            jsonBody.put("temperature", 0.2);

            JSONArray messages = new JSONArray();
            
            JSONObject systemObj = new JSONObject();
            systemObj.put("role", "system");
            systemObj.put("content", systemPrompt);
            messages.put(systemObj);

            JSONObject userObj = new JSONObject();
            userObj.put("role", "user");
            userObj.put("content", "Analyze " + symbol + " using the live prices provided.");
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
    
