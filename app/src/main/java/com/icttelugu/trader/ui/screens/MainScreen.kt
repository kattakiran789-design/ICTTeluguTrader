package com.icttelugu.trader.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.icttelugu.trader.R
import com.icttelugu.trader.ui.theme.*
import com.icttelugu.trader.viewmodel.MainViewModel
import com.icttelugu.trader.viewmodel.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ICT Telugu Trader AI", fontWeight = FontWeight.Bold, color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Disclaimer Banner at Top
            DisclaimerCard()

            // Chat & Analysis View
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }
                if (isLoading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally),
                            color = BullishGreen
                        )
                    }
                }
            }

            // Input Bar
            InputArea(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank() || selectedBitmap != null) {
                        viewModel.sendAnalysisRequest(inputText, selectedBitmap)
                        inputText = ""
                        selectedBitmap = null
                    }
                }
            )
        }
    }
}

@Composable
fun DisclaimerCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = BearishRed.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = stringResource(id = R.string.disclaimer_title),
                color = BearishRed,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(id = R.string.disclaimer_body),
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val bgColor = if (message.isUser) DarkCard else DarkSurface
    val align = if (message.isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .background(bgColor, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
                .widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun InputArea(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text(stringResource(R.string.placeholder_input), fontSize = 12.sp, color = TextSecondary) },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkBackground,
                unfocusedContainerColor = DarkBackground,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
        IconButton(onClick = onSend) {
            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = BullishGreen)
        }
    }
}

