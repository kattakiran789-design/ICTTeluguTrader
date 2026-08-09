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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

        addMessage("Bot", "Namaste! Nenu mi ICT AI Trader. Stock or Index name type cheyandi (e.g., RELIANCE, TCS, NIFTY, BANKNIFTY).");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userQuery = inputMessage.getText().toString().trim();
                if (!userQuery.isEmpty()) {
                    addMessage("You", userQuery);
                    inputMessage.setText("");
                    addMessage("Bot", "Live Market Data teeskuntondi... Dayachesi vechi undandi.");
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

    // Step 1: Google Finance నుండి JSoup ద్వారా Live Price ఫెచ్ చేయడం
    private void fetchLiveMarketData(String symbol) {
        String searchSymbol = symbol.toUpperCase().trim();

        // Nifty, Bank Nifty మరియు ఇతర స్టాక్స్ కోసం Mapping
        if (searchSymbol.equals("NIFTY") || searchSymbol.equals("NIFTY 50") || searchSymbol.equals("NIFTY50")) {
            searchSymbol = "NIFTY_50:INDEXNSE";
        } else if (searchSymbol.equals("BANKNIFTY") || searchSymbol.equals("BANK NIFTY") || searchSymbol.equals("NIFTYBANK")) {
            searchSymbol = "NIFTY_BANK:INDEXNSE";
        } else if (!searchSymbol.contains(":")) {
            searchSymbol = searchSymbol + ":NSE";
        }

        String googleFinanceUrl = "https://www.google.com/finance/quote/" + searchSymbol;
        String finalSymbol = searchSymbol;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(googleFinanceUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                            .get();

                    Element priceElement = doc.select("div.YMlA2d").first();

                    if (priceElement != null) {
                        String priceText = priceElement.text().replaceAll("[^0-9.]", "");
                        double ltp = Double.parseDouble(priceText);

                        addMessage("Bot", "Live Data Received! Current Price (LTP): ₹" + ltp + "\nAI Real-time Intraday Analysis chestondi...");
                        getAiAnalysis(finalSymbol, ltp, true);
                    } else {
                        getAiMessageFallback(finalSymbol);
                    }
                } catch (Exception e) {
                    getAiMessageFallback(finalSymbol);
                }
            }
        }).start();
    }

    private void getAiMessageFallback(String symbol) {
        addMessage("Bot", "Live price fetch failed. Standard ICT Analysis పంపిస్తున్నాము...");
        getAiAnalysis(symbol, 0, false);
    }

    // Step 2: Live Price ని Prompts లోకి పంపి AI Analysis పొందడం
    private void getAiAnalysis(String symbol, double ltp, boolean hasLiveData) {
        String liveContext = "";
        if (hasLiveData) {
            liveContext = "CRITICAL INSTRUCTION: The REAL-TIME Live Market Price (LTP) for " + symbol + " is EXTREMELY EXACTLY: ₹" + ltp + ".\n" +
                    "All Order Blocks, Entry Points, Stop Loss, and Targets MUST be strictly calculated near this exact Current Price of ₹" + ltp + ". Do not invent random prices.";
        } else {
            liveContext = "Live price fetch failed. Provide relative structural levels for " + symbol + ".";
        }

        String systemPrompt = "You are an expert ICT Strategy Analyst for Indian Stock Markets.\n" + liveContext + "\n\n" +
                "Provide output ONLY in TELUGU mixed with English technical terms in this exact format:\n\n" +
                "📊 **" + symbol.toUpperCase() + " REAL-TIME ICT ANALYSIS**\n" +
                "💰 **Live Market Price (LTP): ₹" + ltp + "**\n\n" +
                "1. **Market Trend & Liquidity Sweep:**\n" +
                "   - Trend: (Bullish / Bearish)\n" +
                "   - Liquidity Level Taken\n\n" +
                "2. **Order Block (OB) Identification:**\n" +
                "   - **Bullish Order Block Zone:** (Demand zone near " + ltp + ")\n" +
                "   - **Bearish Order Block Zone:** (Supply zone near " + ltp + ")\n" +
                "   - **Status:** Unmitigated / Mitigated\n\n" +
                "3. **Fair Value Gap (FVG):**\n" +
                "   - Imbalance Zone around Current Price\n\n" +
                "4. **Exact Intraday Trade Plan:**\n" +
                "   - **Entry Point:** (Near " + ltp + ")\n" +
                "   - **Stop Loss (SL):** (Strict level)\n" +
                "   - **Targets (TP1 & TP2):**\n" +
                "   - **Risk-Reward:**\n\n" +
                "5. **Confirmation:**\n" +
                "   - 5-Min Choch / MSS Trigger level";

        String url = "https://api.groq.com/openai/v1/chat/completions";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.3-70b-versatile");
            jsonBody.put("temperature", 0.1);

            JSONArray messages = new JSONArray();

            JSONObject systemObj = new JSONObject();
            systemObj.put("role", "system");
            systemObj.put("content", systemPrompt);
            messages.put(systemObj);

            JSONObject userObj = new JSONObject();
            userObj.put("role", "user");
            userObj.put("content", "Give ICT Analysis for " + symbol + " based on LTP " + ltp);
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
                    addMessage("Bot", "Connection Error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response.body().string());
                            String aiReply = jsonResponse.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            addMessage("Bot", aiReply);
                        } catch (Exception e) {
                            addMessage("Bot", "Parsing Error: " + e.getMessage());
                        }
                    } else {
                        addMessage("Bot", "API Failed with code: " + response.code());
                    }
                }
            });

        } catch (Exception e) {
            addMessage("Bot", "Error: " + e.getMessage());
        }
    }
    }
                    
