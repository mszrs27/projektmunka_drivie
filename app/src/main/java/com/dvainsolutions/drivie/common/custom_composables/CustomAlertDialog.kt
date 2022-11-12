package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dvainsolutions.drivie.R

@Composable
fun CustomAlertDialog(
    title: String,
    description: String,
    confirmText: String = stringResource(R.string.btn_yes),
    dismissText: String = stringResource(R.string.btn_no),
    onConfirmFunction: () -> Unit,
    onDismissFunction: (() -> Unit)? = null
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = description) },
        onDismissRequest = {
            onDismissFunction?.invoke()
        },
        confirmButton = {
            TextButton(onClick = onConfirmFunction)
            { Text(text = confirmText) }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissFunction?.invoke()
            })
            { Text(text = dismissText) }
        }
    )

}

@Preview
@Composable
fun CustomAlertDialogPreview() {
    CustomAlertDialog(title = "", description = "", onConfirmFunction = {  })
}