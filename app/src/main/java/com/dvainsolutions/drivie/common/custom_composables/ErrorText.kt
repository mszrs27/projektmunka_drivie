package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorText(message: String) {
    Text(
        modifier = Modifier.padding(top = 5.dp, start = 5.dp),
        text = message, style = TextStyle(
            color = Color.Red,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp,
        )
    )
}