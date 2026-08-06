package com.icttelugu.trader.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(private val apiKey: String) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    suspend fun analyzeChartOrPrompt(promptText: String, chartBitmap: Bitmap? = null): String {
        return withContext(Dispatchers.IO) {
            try {
                val fullPrompt = "${SystemPrompts.ICT_TRADER_SYSTEM_PROMPT}\n\nUser Request: $promptText"
                val response = if (chartBitmap != null) {
                    val inputContent = content {
                        image(chartBitmap)
                        text(fullPrompt)
                    }
                    generativeModel.generateContent(inputContent)
                } else {
                    generativeModel.generateContent(fullPrompt)
                }
                response.text ?: "క్షమించండి, ప్రతిస్పందనను జెనెరేట్ చేయడం సాధ్యపడలేదు."
            } catch (e: Exception) {
                "ఎర్రర్ సంభవించింది: ${e.localizedMessage}"
            }
        }
    }
}

