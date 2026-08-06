package com.icttelugu.trader

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // స్క్రీన్ పై టెక్స్ట్ చూపించడానికి చిన్న లేఅవుట్
        val textView = TextView(this)
        textView.text = "Welcome to ICT Telugu Trader!"
        textView.textSize = 20f
        textView.setPadding(50, 100, 50, 50)
        
        setContentView(textView)
    }
}

