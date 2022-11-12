package com.dvainsolutions.drivie.presentation.refuel

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.*
import com.dvainsolutions.drivie.data.model.FuelTypeList
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.utils.CustomFileProvider
import com.dvainsolutions.drivie.utils.NoRippleInteractionSource
import com.dvainsolutions.drivie.utils.rememberTakePictureActivityResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RefuelScreen(
    navController: NavHostController,
    viewModel: RefuelViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.value

    val permissionsState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    val cameraContent = rememberTakePictureActivityResult(onResult = {
        viewModel.hasCameraImage = it
        viewModel.runTextRecognition()
    })

    fun onBackPress() {
        if (!viewModel.shouldShowClosePromptDialog()) navController.navigateUp()
    }

    BackHandler(
        onBack = ::onBackPress
    )

    if (viewModel.isUnsaved.value) {
        CustomAlertDialog(
            title = stringResource(R.string.dialog_title_unsaved_data),
            description = stringResource(R.string.dialog_description_msg),
            onConfirmFunction = { navController.navigateUp() },
            onDismissFunction = { viewModel.isUnsaved.value = false }
        )
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_refuel),
                onNavigationIconClick = ::onBackPress
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.vehicleList.isNotEmpty())
                CustomSpinner(
                    dataList = viewModel.vehicleList.keys.toList(),
                    onSelected = viewModel::onVehicleChange
                )
            else
                CircularProgressIndicator()
            TextButton(
                onClick = {
                    permissionsState.launchPermissionRequest()
                    if (permissionsState.status.isGranted) {
                        val uri = CustomFileProvider.getImageUri(context)
                        viewModel.cameraResultUriForTextRecognition.value = uri
                        cameraContent.launch(uri)
                    }
                },
                interactionSource = remember {
                    NoRippleInteractionSource()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.text_read_data_with_cam),
                    style = MaterialTheme.typography.body1
                )
            }
            DateTimePicker(
                time = uiState.date,
                placeholderRes = R.string.placeholder_date,
                onTimeChange = viewModel::onDateChange,
                placeholderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
            )
            CustomTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                text = uiState.location,
                label = { Text(text = stringResource(id = R.string.placeholder_refuel_place)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                onValueChangeFunction = viewModel::onLocationChange
            )
            LazyColumn(Modifier.fillMaxWidth()) {
                items(items = viewModel.locations) { address ->
                    TextButton(onClick = {
                        viewModel.selectAddress(address.getAddressLine(0))
                    }) {
                        Text(text = address.getAddressLine(0))
                    }
                }
            }
            CustomSpinner(
                dataList = FuelTypeList(context).fuel,
                inputData = viewModel.uiState.value.type,
                onSelected = viewModel::onFuelTypeChange
            )
            CustomTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                text = uiState.quantity,
                label = { Text(text = stringResource(id = R.string.placeholder_refuel_fuel_amount)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Decimal
                ),
                onValueChangeFunction = viewModel::onFuelQuantityChange
            )
            CustomTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                text = uiState.cost,
                label = { Text(text = stringResource(id = R.string.placeholder_refuel_total_cost)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                onValueChangeFunction = viewModel::onCostChange
            )
            CustomTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                text = uiState.mileage,
                label = { Text(text = stringResource(id = R.string.vehicle_mileage)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                onValueChangeFunction = viewModel::onMileageChange
            )

            CustomButton(
                modifier = Modifier.weight(1f, false),
                textResource = R.string.btn_save_data,
                onClick = {
                    viewModel.saveRefuelData { taskId ->
                        navController.popBackStack()
                        navController.navigate("${Screen.RefuelDetailsScreen.route}/${true}/${taskId}")
                    }
                },
                isLoading = viewModel.isButtonLoading.value
            )
        }
    }
}

@Preview
@Composable
fun RefuelScreenPreview() {
    RefuelScreen(rememberNavController())
}