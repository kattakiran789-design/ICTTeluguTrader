package com.icttelugu.trader.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icttelugu.trader.data.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Message(val text: String, val isUser: Boolean, val image: Bitmap? = null)

class MainViewModel : ViewModel() {

    // మీ Gemini API Key ఇక్కడ నమోదు చేయండి
    private val repository = GeminiRepository(apiKey = "YOUR_GEMINI_API_KEY")

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendAnalysisRequest(userText: String, bitmap: Bitmap? = null) {
        val userMsg = Message(text = userText, isUser = true, image = bitmap)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            val responseText = repository.analyzeChartOrPrompt(userText, bitmap)
            val aiMsg = Message(text = responseText, isUser = false)
            _messages.value = _messages.value + aiMsg
            _isLoading.value = false
        }
    }
}

