package com.dvainsolutions.drivie.presentation.vehicles.details

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.*
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarMessage
import com.dvainsolutions.drivie.data.model.EmissionStandard
import com.dvainsolutions.drivie.data.model.FuelTypeList
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.ui.theme.DrivieGray
import com.dvainsolutions.drivie.utils.CustomFileProvider
import com.dvainsolutions.drivie.utils.UiText
import com.dvainsolutions.drivie.utils.rememberGetContentActivityResult
import com.dvainsolutions.drivie.utils.rememberTakePictureActivityResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun VehicleDetailsScreen(
    navController: NavHostController,
    carId: String?,
    viewModel: VehicleDetailsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value
    val bottomSheetScaffoldState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val permissionsState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    val galleryContent = rememberGetContentActivityResult(onResult = {
        viewModel.galleryResultUri.value = it
    })
    val cameraContent = rememberTakePictureActivityResult(onResult = {
        viewModel.hasCameraImage = it
    })

    LaunchedEffect(true) {
        if (!carId.isNullOrEmpty())
            viewModel.getVehicleDetails(carId)
    }

    fun onBackPress() {
        if (!viewModel.hasDataChanged.value) {
            showDialog.value = false
            navController.navigateUp()
        } else {
            showDialog.value = true
        }
    }

    if (showDialog.value) {
        CustomAlertDialog(
            title = stringResource(R.string.dialog_title_unsaved_data),
            description = stringResource(R.string.dialog_description_msg),
            onConfirmFunction = { navController.navigateUp() },
            onDismissFunction = { showDialog.value = false }
        )
    }

    BackHandler(
        onBack = {
            coroutineScope.launch {
                if (bottomSheetScaffoldState.isVisible) {
                    bottomSheetScaffoldState.hide()
                } else {
                    onBackPress()
                }
            }
        }
    )

    ModalBottomSheetLayout(
        sheetState = bottomSheetScaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(DrivieGray),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = {
                        if (permissionsState.status.isGranted) {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.hide()
                            }
                            viewModel.hasCameraImage = false
                            viewModel.galleryResultUri.value = null
                            val uri = CustomFileProvider.getImageUri(context)
                            viewModel.cameraResultUri.value = uri
                            cameraContent.launch(uri)
                        } else {
                            SnackbarManager.showMessage(
                                SnackbarMessage.StringSnackbar(
                                    UiText.StringResource(resId = R.string.permission_msg_camera)
                                        .asString(context)
                                )
                            )
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.btn_camera))
                }
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.hide()
                        }
                        viewModel.hasCameraImage = false
                        galleryContent.launch("image/*")
                    }
                ) {
                    Text(text = stringResource(id = R.string.btn_gallery))
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                CustomAppBar(
                    titleText = stringResource(R.string.app_bar_title_selected_vehicle),
                    onNavigationIconClick = ::onBackPress
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.isLoading)
                            CircularProgressIndicator()
                        else {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(
                                        if (uiState.pictureUrl.isNotEmpty() && (viewModel.galleryResultUri.value == null && viewModel.cameraResultUri.value == null))
                                            uiState.pictureUrl
                                        else
                                            if (viewModel.hasCameraImage) viewModel.cameraResultUri.value else viewModel.galleryResultUri.value
                                    )
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.ic_car),
                                error = painterResource(R.drawable.ic_car),
                                contentDescription = stringResource(R.string.desc_profile_picture),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .background(DrivieGray)
                                    .clickable(onClick = {
                                        coroutineScope.launch {
                                            permissionsState.launchPermissionRequest()
                                            if (bottomSheetScaffoldState.isVisible) {
                                                bottomSheetScaffoldState.hide()
                                            } else {
                                                bottomSheetScaffoldState.show()
                                            }
                                        }
                                    })
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.carName,
                                label = { Text(text = stringResource(id = R.string.vehicle_name)) },
                                onValueChangeFunction = viewModel::onCarNameChange,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.licence,
                                label = { Text(text = stringResource(id = R.string.vehicle_licence)) },
                                onValueChangeFunction = viewModel::onLicenceChange,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.brand,
                                onValueChangeFunction = viewModel::onBrandChange,
                                label = { Text(text = stringResource(id = R.string.vehicle_brand)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.model,
                                onValueChangeFunction = viewModel::onModelChange,
                                label = { Text(text = stringResource(id = R.string.vehicle_model)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.engine,
                                onValueChangeFunction = viewModel::onEngineChange,
                                label = { Text(text = stringResource(id = R.string.vehicle_engine)) },
                                trailingIcon = {
                                    val superscript = SpanStyle(
                                        baselineShift = BaselineShift.Superscript,
                                        fontSize = 12.sp
                                    )
                                    val unitString = buildAnnotatedString {
                                        append("cm")
                                        withStyle(superscript) {
                                            append("3")
                                        }
                                    }
                                    Text(text = unitString)
                                },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.consumption,
                                onValueChangeFunction = viewModel::onConsumptionChange,
                                label = { Text(text = stringResource(id = R.string.vehicle_consumption)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    keyboardType = KeyboardType.Number
                                ),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.mileage,
                                onValueChangeFunction = viewModel::onMileageChange,
                                label = { Text(text = stringResource(id = R.string.vehicle_mileage)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    keyboardType = KeyboardType.Number
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomSpinner(
                                dataList = FuelTypeList(context).fuel,
                                inputData = uiState.fuelType,
                                onSelected = viewModel::onFuelTypeChange
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.fuelSize,
                                onValueChangeFunction = viewModel::onFuelSizeChange,
                                label = { Text(text = stringResource(id = R.string.vehicle_fuel_size)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    keyboardType = KeyboardType.Number
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            DateTimePicker(
                                time = uiState.motDate,
                                placeholderRes = R.string.vehicle_mot,
                                onTimeChange = viewModel::onMotDateChange
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomSpinner(
                                dataList = EmissionStandard.values().map { it.standard }.toList(),
                                inputData = uiState.emissionStandard,
                                onSelected = viewModel::onEmissionStandardChange
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            DateTimePicker(
                                time = uiState.insurance,
                                label = { Text(text = stringResource(id = R.string.vehicle_insurance)) },
                                onTimeChange = viewModel::onInsuranceChange
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            if (viewModel.hasDataChanged.value) {
                                CustomButton(
                                    textResource = R.string.btn_save_data,
                                    onClick = {
                                        if (carId != null) {
                                            viewModel.updateVehicleDetails(carId)
                                        }
                                    },
                                    isLoading = viewModel.isButtonLoading.value
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            CustomButton(
                                textResource = R.string.btn_vehicle_stats,
                                onClick = {
                                    navController.navigate(Screen.StatisticsScreen.route)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun VehicleDetailsScreenPreview() {
    VehicleDetailsScreen(rememberNavController(), "")
}