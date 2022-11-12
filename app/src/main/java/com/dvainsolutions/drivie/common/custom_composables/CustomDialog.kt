package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.utils.NoRippleInteractionSource

@Composable
fun CustomDialog(
    title: String,
    positiveButtonText: String = stringResource(R.string.btn_save),
    negativeButtonText: String = stringResource(id = R.string.btn_cancel),
    content: @Composable () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
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
                        Text(text = title, style = MaterialTheme.typography.h6)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomSpinner(dataList = listOf("Biztosítás", "Matrica", "Súlyadó"))
                    Spacer(modifier = Modifier.height(10.dp))
                    DateTimePicker(time = "", placeholderRes = R.string.placeholder_date, onTimeChange = {/*TODO*/})
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomSpinner(dataList = listOf("Országos - D1M - Heti", "Országos - D1M - Havi", "Országos - D1M - Éves", "Megyei - D1M - Éves"))
                    Spacer(modifier = Modifier.height(10.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = "",
                        label = { Text(text = stringResource(id = R.string.placeholder_cost)) },
                        trailingIcon = {
                            Text(text = " Ft")
                        }
                    )
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
                    Button(
                        onClick = { openDialog.value = false }
                    ) {
                        Text(positiveButtonText)
                    }
                    TextButton(
                        onClick = { openDialog.value = false },
                        interactionSource = remember {
                            NoRippleInteractionSource()
                        }
                    ) {
                        Text(
                            text = negativeButtonText,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.button
                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun CustomDialogPreview() {
    CustomDialog("Cím", content = {})
}