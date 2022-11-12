package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dvainsolutions.drivie.ui.theme.poppinsFont

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String?,
    label: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,
    maxLines: Int = 1,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChangeFunction: ((String) -> Unit)? = null,
    onClickableFunction: (() -> Unit)? = null,
    onFocusChangedFunction: ((FocusState) -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TextField(
        enabled = enabled,
        modifier = modifier
            .shadow(5.dp, shape = RoundedCornerShape(15.dp))
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null
            ) {
                onClickableFunction?.invoke()
            }
            .onFocusChanged {
                onFocusChangedFunction?.invoke(it)
            },
        value = text ?: "",
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        onValueChange = {
            onValueChangeFunction?.invoke(it)
        },
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            cursorColor = Color.Black,
            disabledPlaceholderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium),
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Red,
            placeholderColor = Color.Gray.copy(alpha = 0.7f)
        ),
        maxLines = maxLines,
        singleLine = singleLine,
        isError = isError,
        textStyle = TextStyle(
            fontFamily = poppinsFont,
            color = Color.Black,
            fontWeight = FontWeight.Light
        ),
        visualTransformation = visualTransformation
    )
}

@Preview(showBackground = false)
@Composable
fun CustomTextFieldComposable() {
    CustomTextField(text = "Custom text")
}