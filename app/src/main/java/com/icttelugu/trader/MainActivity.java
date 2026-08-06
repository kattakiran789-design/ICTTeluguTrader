package com.icttelugu.trader;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("ICT Telugu Trader App Started Successfully!");
        textView.setTextSize(20);
        textView.setPadding(50, 100, 50, 50);

        setContentView(textView);
    }
}
