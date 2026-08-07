package com.icttelugu.trader;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private EditText inputMessage;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dynamic Main Layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(20, 20, 20, 20);

        // Header
        TextView header = new TextView(this);
        header.setText("📈 ICT AI Trading Assistant");
        header.setTextSize(22f);
        header.setPadding(0, 10, 0, 20);
        mainLayout.addView(header);

        // Chat History View
        scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        scrollView.setLayoutParams(scrollParams);

        chatContainer = new LinearLayout(this);
        chatContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(chatContainer);
        mainLayout.addView(scrollView);

        // Input Layout
        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);

        inputMessage = new EditText(this);
        inputMessage.setHint("ఉదా: Nifty 50 ICT & FVG Analysis ఇవ్వు...");
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        inputMessage.setLayoutParams(inputParams);

        Button sendButton = new Button(this);
        sendButton.setText("Ask AI");

        inputLayout.addView(inputMessage);
        inputLayout.addView(sendButton);
        mainLayout.addView(inputLayout);

        setContentView(mainLayout);

        // Initial System Message
        addMessage("Bot", "నమస్తే! నేను మీ AI Trading Analyzer ని.\n" +
                "నేను ICT, Order Flow, Supply/Demand, FVG, Liquidity, Anchored VWAP, Volume Profile & Moving Averages ఆధారంగా Indian Stocks & Indexes ని అనలైజ్ చేయగలను. ఏ స్టాక్ లేదా ఇండెక్స్ గురించిన అనాలిసిస్ కావాలో టైప్ చేయండి!");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = inputMessage.getText().toString().trim();
                if (!query.isEmpty()) {
                    addMessage("You", query);
                    inputMessage.setText("");
                    generateAIResponse(query);
                }
            }
        });
    }

    private void addMessage(String sender, String message) {
        TextView textView = new TextView(this);
        textView.setText(sender + ":\n" + message + "\n");
        textView.setTextSize(16f);
        textView.setPadding(15, 15, 15, 15);
        
        if (sender.equals("You")) {
            textView.setBackgroundColor(0xFFE3F2FD); // Light Blue
        } else {
            textView.setBackgroundColor(0xFFF1F8E9); // Light Green
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 10, 0, 10);
        textView.setLayoutParams(params);

        chatContainer.addView(textView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void generateAIResponse(String userQuery) {
        // AI Strategy Engine Simulation Response
        String response = "📊 Trading Strategy Analysis for: " + userQuery + "\n\n" +
                "1. ICT & FVG: 5-Min Chart లో నిన్నటి High వద్ద Bullish Fair Value Gap (FVG) క్రియేట్ అయింది.\n" +
                "2. Liquidity & Order Flow: Sell-side Liquidity Sweep పూర్తయింది. Institutional Order Flow బుల్లిష్‌గా ఉంది.\n" +
                "3. Supply & Demand: H4 Demand Zone వద్ద సపోర్ట్ తీసుకుంటోంది.\n" +
                "4. Anchored VWAP & Volume Profile: POC (Point of Control) పైన ట్రేడ్ అవుతోంది.\n" +
                "5. Fibonacci Level: 0.618 Golden Pocket రిట్రేస్‌మెంట్ దగ్గర కన్సాలిడేట్ అవుతోంది.\n\n" +
                "🎯 Execution Setup:\n" +
                "• Signal: HIGH CONFIDENCE BUY\n" +
                "• Entry Zone: FVG / Demand Level Re-test లో\n" +
                "• Stop Loss (SL): Demand Zone కింద\n" +
                "• Target (TP): Liquidity High Pool";

        addMessage("Bot", response);
    }
}
