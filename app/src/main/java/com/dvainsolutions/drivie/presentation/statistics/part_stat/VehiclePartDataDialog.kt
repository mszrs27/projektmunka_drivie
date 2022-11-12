package com.dvainsolutions.drivie.presentation.statistics.part_stat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomProgressBar
import com.dvainsolutions.drivie.common.custom_composables.CustomTextField
import com.dvainsolutions.drivie.common.ext.toFormattedString
import com.dvainsolutions.drivie.ui.theme.DrivieDarkBlue
import com.dvainsolutions.drivie.ui.theme.DrivieLightBlue
import com.dvainsolutions.drivie.utils.NoRippleInteractionSource

@Composable
fun VehiclePartDataDialog(
    viewModel: PartsViewModel,
    onDismissFunction: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = {
            onDismissFunction?.invoke()
        },
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = viewModel.selectedPart.name, style = MaterialTheme.typography.h6)
                }
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    text = viewModel.selectedPart.currentHealth?.let { health ->
                        viewModel.selectedPart.maxLifeSpan - health
                    }.toString(),
                    enabled = false,
                    label = {
                        Text(
                            stringResource(id = R.string.vehicle_mileage)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                CustomTextField(
                    text = viewModel.selectedPart.replacementTime?.toDate()?.toFormattedString(),
                    enabled = false,
                    label = {
                        Text(
                            stringResource(id = R.string.vehicle_part_replacement_time)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = stringResource(R.string.vehicle_part_wear))
                }
                CustomProgressBar(
                    modifier = Modifier.clip(shape = RoundedCornerShape(14.dp)),
                    width = 250.dp,
                    backgroundColor = Color.LightGray,
                    foregroundColor = Brush.horizontalGradient(
                        listOf(
                            DrivieLightBlue,
                            DrivieDarkBlue
                        )
                    ),
                    percent = (100.0 - (viewModel.selectedPart.currentHealth!! / viewModel.selectedPart.maxLifeSpan.toDouble()) * 100).toInt()
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    text = viewModel.selectedPart.currentHealth?.let { viewModel.predict(it) },
                    enabled = false,
                    label = {
                        Text(
                            stringResource(id = R.string.vehicle_parts_wear_date)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
        },
        buttons = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = { onDismissFunction?.invoke() },
                    interactionSource = remember {
                        NoRippleInteractionSource()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.btn_exit),
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    )
}