package com.dvainsolutions.drivie.common.custom_composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.toSize
import com.dvainsolutions.drivie.R


@Composable
fun DropDownList(
    modifier: Modifier = Modifier,
    requestToOpen: Boolean = false,
    list: List<Any>?,
    request: (Boolean) -> Unit,
    selectedString: (String) -> Unit,
    textFieldSize: MutableState<Size>
) {
    DropdownMenu(
        modifier = modifier.width(with(LocalDensity.current) { textFieldSize.value.width.toDp() }),
        expanded = requestToOpen,
        onDismissRequest = { request(false) },
    ) {
        list?.forEach {
            DropdownMenuItem(modifier = modifier.fillMaxWidth(), onClick = {
                request(false)
                selectedString(it.toString())
            }) {
                Text(it.toString(), modifier = modifier.wrapContentWidth())
            }
        }
    }
}

@Composable
fun CustomSpinner(
    modifier: Modifier = Modifier,
    onSelected: ((String) -> Unit)? = null,
    dataList: List<Any>?,
    inputData: String? = null
) {
    val text = remember { mutableStateOf(dataList?.first().toString()) }
    val isOpen = remember { mutableStateOf(false) }
    val textFieldSize = remember { mutableStateOf(Size.Zero) }
    val openCloseOfDropDownList: (Boolean) -> Unit = {
        isOpen.value = it
    }

    val userSelectedString: (String) -> Unit = {
        text.value = it
        if (onSelected != null) {
            onSelected(it)
        }
    }
    Box(modifier = modifier
        .onGloballyPositioned { coordinates ->
            textFieldSize.value = coordinates.size.toSize()
        }
        .fillMaxWidth()) {
        Column {
            CustomTextField(
                enabled = false,
                text = inputData ?: text.value,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, stringResource(R.string.desc_dropdown_items))
                },
                modifier = Modifier.fillMaxWidth(),
                onValueChangeFunction = { text.value = it },
                onClickableFunction = {
                    isOpen.value = true
                }
            )
            DropDownList(
                requestToOpen = isOpen.value,
                list = dataList,
                request = openCloseOfDropDownList,
                selectedString = userSelectedString,
                textFieldSize = textFieldSize
            )
        }
    }
}