package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dvainsolutions.drivie.ui.theme.DrivieDarkBlue
import com.dvainsolutions.drivie.ui.theme.DrivieLightBlue

@Composable
fun CustomProgressBar(
    modifier: Modifier,
    width: Dp,
    backgroundColor: Color,
    foregroundColor: Brush,
    percent: Int
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .width(width)
    ) {
        Box(
            modifier = modifier
                .background(foregroundColor)
                .width(width * percent / 100)
        ) {
            Text("")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomProgressBarPreview() {
    CustomProgressBar(
        modifier = Modifier.clip(shape = RoundedCornerShape(14.dp)),
        width = 100.dp,
        backgroundColor = Color.Red,
        foregroundColor = Brush.horizontalGradient(listOf(DrivieLightBlue, DrivieDarkBlue)),
        percent = 70
    )
}