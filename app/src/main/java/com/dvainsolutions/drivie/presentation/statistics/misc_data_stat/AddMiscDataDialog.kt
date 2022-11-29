package com.dvainsolutions.drivie.presentation.statistics.misc_data_stat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomButton
import com.dvainsolutions.drivie.common.custom_composables.CustomSpinner
import com.dvainsolutions.drivie.common.custom_composables.CustomTextField
import com.dvainsolutions.drivie.common.custom_composables.DateTimePicker
import com.dvainsolutions.drivie.data.model.InsuranceType
import com.dvainsolutions.drivie.data.model.MiscTypeList
import com.dvainsolutions.drivie.data.model.VignetteRegionalType
import com.dvainsolutions.drivie.data.model.VignetteVehicleType
import com.dvainsolutions.drivie.utils.NoRippleInteractionSource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddMiscDataDialog(
    viewModel: MiscDataViewModel,
    positiveButtonText: String = stringResource(R.string.btn_save),
    negativeButtonText: String = stringResource(id = R.string.btn_cancel),
    onConfirmFunction: () -> Unit,
    onDismissFunction: (() -> Unit)? = null
) {
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.getVehicleList()
    }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.width(350.dp),
        onDismissRequest = { onDismissFunction?.invoke() },
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
                    Text(
                        text = stringResource(R.string.dialog_title_add),
                        style = MaterialTheme.typography.h6
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (viewModel.vehicleList.isNotEmpty())
                    CustomSpinner(
                        dataList = viewModel.vehicleList.keys.toList(),
                        onSelected = viewModel::onVehicleChange
                    )
                else
                    CircularProgressIndicator()
                Spacer(modifier = Modifier.height(10.dp))
                CustomSpinner(
                    dataList = MiscTypeList.values().map { it.getLabel(context) }
                        .toList(),
                    onSelected = viewModel::onMiscTypeChange
                )
                Spacer(modifier = Modifier.height(10.dp))
                when (viewModel.miscType) {
                    MiscTypeList.INSURANCE.getLabel(context) -> {
                        CustomSpinner(
                            dataList = InsuranceType.values().map { it.getLabel(context) }
                                .toList(),
                            onSelected = viewModel::onInsuranceTypeChange
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        PriceTextField()
                        Spacer(modifier = Modifier.height(10.dp))
                        DateTextField()
                    }
                    MiscTypeList.VIGNETTE.getLabel(context) -> {
                        CustomSpinner(
                            dataList = VignetteVehicleType.values().toList(),
                            onSelected = viewModel::onVignetteVehicleTypeChange
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        CustomSpinner(
                            dataList = VignetteRegionalType.values().map { it.getLabel(context) }.toList(),
                            onSelected = viewModel::onVignetteVehicleTypeChange
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        PriceTextField()
                        Spacer(modifier = Modifier.height(10.dp))
                        DateTextField()
                        Spacer(modifier = Modifier.height(10.dp))
                        DateTimePicker(
                            time = viewModel.uiState.endDate,
                            placeholderRes = R.string.placeholder_date,
                            onTimeChange = viewModel::onEndDateChange,
                            placeholderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                        )
                    }
                    MiscTypeList.WEIGHT_TAX.getLabel(context) -> {
                        PriceTextField()
                        Spacer(modifier = Modifier.height(10.dp))
                        DateTextField()
                    }
                }
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
                CustomButton(
                    textResource = R.string.btn_save,
                    onClick = onConfirmFunction,
                    isLoading = viewModel.uiState.isLoading
                )
                TextButton(
                    onClick = { onDismissFunction?.invoke() },
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

@Composable
fun PriceTextField(viewModel: MiscDataViewModel = hiltViewModel()) {
    CustomTextField(
        modifier = Modifier.fillMaxWidth(),
        text = viewModel.uiState.price,
        onValueChangeFunction = viewModel::onPriceChange,
        label = { Text(text = stringResource(id = R.string.placeholder_paid_amount)) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Number
        ),
        trailingIcon = {
            Text(text = " Ft")
        }
    )
}

@Composable
fun DateTextField(viewModel: MiscDataViewModel = hiltViewModel()) {
    DateTimePicker(
        time = viewModel.uiState.date,
        placeholderRes = R.string.placeholder_date,
        onTimeChange = viewModel::onDateChange,
        placeholderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
    )
}