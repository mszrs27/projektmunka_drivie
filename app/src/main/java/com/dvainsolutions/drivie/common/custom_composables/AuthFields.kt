package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.dvainsolutions.drivie.R.string as AppRes

@Composable
fun EmailField(value: String?, onNewValue: (String) -> Unit, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current

    CustomTextField(
        modifier = modifier.fillMaxWidth(),
        text = value,
        onValueChangeFunction = { onNewValue(it) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email, contentDescription = stringResource(
                    AppRes.desc_email
                )
            )
        },
        placeholder = {
            Text(text = stringResource(id = AppRes.placeholder_email))
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(focusDirection = FocusDirection.Next) }),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordField(
    value: String,
    @androidx.annotation.StringRes placeholder: Int,
    onNewValue: (String) -> Unit,
    keyboardAction: ImeAction = ImeAction.Done,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val icon = if (isVisible) rememberVectorPainter(Icons.Default.VisibilityOff)
    else rememberVectorPainter(Icons.Default.Visibility)

    val visualTransformation = if (isVisible) VisualTransformation.None
    else PasswordVisualTransformation()

    CustomTextField(
        modifier = modifier.fillMaxWidth(),
        text = value,
        onValueChangeFunction = { onNewValue(it) },
        placeholder = { Text(text = stringResource(placeholder)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(AppRes.placeholder_password)
            )
        },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(icon, contentDescription = stringResource(AppRes.desc_visibility))
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = keyboardAction
        ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        visualTransformation = visualTransformation
    )
}