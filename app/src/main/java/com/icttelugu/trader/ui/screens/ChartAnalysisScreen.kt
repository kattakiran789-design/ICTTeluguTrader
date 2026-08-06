package com.icttelugu.trader.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.icttelugu.trader.ui.theme.BullishGreen

@Composable
fun ChartAnalysisScreen(
    selectedImage: Bitmap?,
    onSelectImageClick: () -> Unit,
    onAnalyzeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (selectedImage != null) {
            Image(
                bitmap = selectedImage.asImageBitmap(),
                contentDescription = "Selected Chart",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = onSelectImageClick,
            colors = ButtonDefaults.buttonColors(containerColor = BullishGreen)
        ) {
            Text("చార్ట్ ఇమేజ్ ఎంచుకోండి (Upload Screenshot)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedImage != null) {
            Button(onClick = onAnalyzeClick) {
                Text("ICT విశ్లేషణ ప్రారంభించు (Analyze)")
            }
        }
    }
}
