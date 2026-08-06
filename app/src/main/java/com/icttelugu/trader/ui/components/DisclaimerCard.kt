package com.icttelugu.trader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.icttelugu.trader.R
import com.icttelugu.trader.ui.theme.BearishRed
import com.icttelugu.trader.ui.theme.TextSecondary

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
