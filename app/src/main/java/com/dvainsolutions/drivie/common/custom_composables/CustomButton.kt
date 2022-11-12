package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(modifier: Modifier = Modifier, textResource: Int, isEnabled: Boolean = true, isLoading: Boolean = false, onClick: () -> Unit) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .shadow(15.dp, RoundedCornerShape(15.dp)),
        onClick = onClick,
        shape = RoundedCornerShape(15.dp),
        enabled = isEnabled,
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Text(
                text = stringResource(
                    id = textResource
                ),
                style = MaterialTheme.typography.button
            )
        }
    }
}