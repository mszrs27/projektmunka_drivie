package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dvainsolutions.drivie.ui.theme.DrivieGrayish
import com.dvainsolutions.drivie.ui.theme.poppinsFont

@Composable
fun CardListItem(
    modifier: Modifier = Modifier,
    title: String,
    additionalContent: Boolean = false,
    additionalText: String = "",
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 12.dp)
            .clickable {
                onClick?.invoke()
            },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = DrivieGrayish,
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = poppinsFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W500
                    )
                )
                if (additionalContent) {
                    Text(
                        text = additionalText,
                        style = TextStyle(
                            fontFamily = poppinsFont,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W500
                        )
                    )
                }
            }
        }
    }
}