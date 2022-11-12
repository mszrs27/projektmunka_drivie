package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.ui.theme.DrivieBlackish
import com.dvainsolutions.drivie.ui.theme.DrivieLightBlue
import com.dvainsolutions.drivie.ui.theme.poppinsFont

@Composable
fun CustomAppBar(
    titleText: String,
    showNavigationIcon: Boolean = true,
    showActionIcon: Boolean = false,
    actionIcon: ImageVector = Icons.Default.Close,
    actionIconDescription: String = stringResource(R.string.app_bar_desc_action),
    onNavigationIconClick: (() -> Unit)? = null,
    onActionIconClick: (() -> Unit)? = null
) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        backgroundColor = DrivieLightBlue,
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(
                    onClick = onNavigationIconClick ?: { }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.app_bar_desc_navigation),
                        tint = DrivieBlackish
                    )
                }
            } else {
                IconButton(
                    enabled = false,
                    onClick = {  }
                ) {}
            }
        },
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                textAlign = TextAlign.Center,
                maxLines = 1,
                text = titleText,
                fontSize = 22.sp,
                style = TextStyle(
                    color = DrivieBlackish,
                    fontFamily = poppinsFont,
                    fontWeight = FontWeight.W500
                )
            )
        },
        actions = {
            if (showActionIcon) {
                IconButton(
                    onClick = onActionIconClick ?: { }
                ) {
                    Icon(
                        actionIcon,
                        contentDescription = actionIconDescription,
                        tint = Color.Black
                    )
                }
            } else {
                IconButton(
                    enabled = false,
                    onClick = {  }
                ) {}
            }
        }
    )
}

@Preview
@Composable
fun CustomAppBarPreview() {
    CustomAppBar(titleText = "Custom app bar")
}