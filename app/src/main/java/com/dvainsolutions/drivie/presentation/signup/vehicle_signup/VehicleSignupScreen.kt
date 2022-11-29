package com.dvainsolutions.drivie.presentation.signup.vehicle_signup

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.*
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarMessage
import com.dvainsolutions.drivie.data.model.VehiclePartList
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.ui.theme.DrivieGray
import com.dvainsolutions.drivie.utils.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import com.dvainsolutions.drivie.R.string as AppRes


@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun VehicleSignupScreen(
    navController: NavHostController,
    isComingFromRegistration: Boolean?,
    viewModel: VehicleSignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val errorState = viewModel.vehicleDataValidationState

    val bottomSheetScaffoldState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val permissionsState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    var shouldRunTextRecognition by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val galleryContent = rememberGetContentActivityResult(onResult = {
        viewModel.galleryResultUri.value = it
    })
    val cameraContent = rememberTakePictureActivityResult(onResult = {
        viewModel.hasCameraImage = it

        if (shouldRunTextRecognition) {
            viewModel.runTextRecognition() {
                shouldRunTextRecognition = false
            }
        }
    })

    BackHandler(
        onBack = {
            coroutineScope.launch {
                if (bottomSheetScaffoldState.isVisible) {
                    bottomSheetScaffoldState.hide()
                } else {
                    navController.popBackStack()
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
                    Text(text = stringResource(R.string.btn_camera))
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
                    Text(text = stringResource(R.string.btn_gallery))
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                CustomAppBar(
                    titleText = stringResource(R.string.app_bar_title_add_vehicle),
                    showNavigationIcon = isComingFromRegistration == false,
                    onNavigationIconClick = { navController.popBackStack() })
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
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(if (viewModel.hasCameraImage) viewModel.cameraResultUri.value else viewModel.galleryResultUri.value)
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
                        TextButton(
                            onClick = {
                                permissionsState.launchPermissionRequest()
                                shouldRunTextRecognition = true
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
                                text = stringResource(R.string.text_read_data_with_cam),
                                style = MaterialTheme.typography.body1
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.carName,
                            onValueChangeFunction = viewModel::onCarNameChange,
                            placeholder = { Text(text = stringResource(id = AppRes.vehicle_name)) },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Column {
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.licence,
                                onValueChangeFunction = viewModel::onLicenceChange,
                                placeholder = { Text(text = stringResource(id = AppRes.vehicle_licence)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                            )
                            if (!errorState.licenceError.isNullOrEmpty()) {
                                ErrorText(message = errorState.licenceError)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Column {
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.brand,
                                onValueChangeFunction = viewModel::onBrandChange,
                                placeholder = { Text(text = stringResource(id = R.string.vehicle_brand)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                            )
                            if (!errorState.brandError.isNullOrEmpty()) {
                                ErrorText(message = errorState.brandError)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Column {
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.model,
                                onValueChangeFunction = viewModel::onModelChange,
                                placeholder = { Text(text = stringResource(id = AppRes.vehicle_model)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next
                                ),
                            )
                            if (!errorState.modelError.isNullOrEmpty()) {
                                ErrorText(message = errorState.modelError)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.engine,
                            onValueChangeFunction = viewModel::onEngineChange,
                            placeholder = { Text(text = stringResource(id = AppRes.vehicle_engine)) },
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
                        Column {
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.mileage,
                                onValueChangeFunction = { text ->
                                    if (text.length <= 9) {
                                        viewModel.onMileageChange(text)
                                    }
                                },
                                placeholder = { Text(text = stringResource(id = AppRes.vehicle_mileage)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    keyboardType = KeyboardType.Number
                                ),
                            )
                            if (!errorState.mileageError.isNullOrEmpty()) {
                                ErrorText(message = errorState.mileageError)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.consumption,
                            onValueChangeFunction = viewModel::onConsumptionChange,
                            placeholder = { Text(text = stringResource(id = AppRes.vehicle_consumption)) },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            ),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Column {
                            CustomTextField(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.fuelType,
                                onValueChangeFunction = viewModel::onFuelTypeChange,
                                placeholder = { Text(text = stringResource(id = AppRes.vehicle_fuel_type)) },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    keyboardType = KeyboardType.Number
                                ),
                            )
                            if (!errorState.fuelTypeError.isNullOrEmpty()) {
                                ErrorText(message = errorState.fuelTypeError)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.fuelSize,
                            onValueChangeFunction = viewModel::onFuelSizeChange,
                            placeholder = { Text(text = stringResource(id = AppRes.vehicle_fuel_size)) },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            ),
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        DateTimePicker(
                            time = uiState.motDate,
                            placeholderRes = R.string.vehicle_mot,
                            onTimeChange = viewModel::onMotDateChange
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomSpinner(
                            dataList = viewModel.emissionList,
                            onSelected = viewModel::onEmissionStandardChange
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        VehiclePartsSection(viewModel)
                        Spacer(modifier = Modifier.height(20.dp))
                        DateTimePicker(
                            time = uiState.insurance,
                            placeholderRes = AppRes.vehicle_insurance,
                            onTimeChange = viewModel::onInsuranceChange
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        CustomButton(
                            textResource = R.string.btn_car_sign_up,
                            onClick = {
                                viewModel.onSaveVehicleData(onNavigation = {
                                    navController.popBackStack()
                                    navController.navigate(Screen.HomeScreen.route)
                                })
                            },
                            isLoading = viewModel.isButtonLoading.value
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        if (isComingFromRegistration == true) {
                            TextButton(
                                onClick = {
                                    navController.navigate(Screen.HomeScreen.route)
                                },
                                interactionSource = remember {
                                    NoRippleInteractionSource()
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.btn_add_later),
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun VehiclePartsSection(viewModel: VehicleSignupViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        CustomSpinner(
            modifier = Modifier.weight(1f),
            dataList = VehiclePartList.parts.map { it.name },
            onSelected = {
                viewModel.setPartNameText(it)
            }
        )
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(
            modifier = Modifier.weight(1f),
            text = viewModel.partMileage.value,
            onValueChangeFunction = {
                if (it.isDigitsOnly())
                    viewModel.setPartMileageText(it)
            },
            label = { Text(text = stringResource(id = R.string.placeholder_part_mileage)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                Text(text = " km")
            }
        )
    }
    Column(horizontalAlignment = Alignment.Start) {
        var dynamicColumnHeight by remember {
            mutableStateOf(10.dp)
        }
        TextButton(
            onClick = {
                keyboardController?.hide()
                viewModel.apply {
                    addItemsToLists(onResult = {
                        dynamicColumnHeight += 40.dp
                    })
                }
            }
        ) {
            Text(stringResource(R.string.btn_add_part))
        }
        LazyColumn(modifier = Modifier.height(dynamicColumnHeight)) {
            items(viewModel.inputParts.keys.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${viewModel.inputParts.keys.elementAt(index)} - ${(viewModel.inputParts.values.elementAt(index))} km",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        dynamicColumnHeight -= 40.dp
                        viewModel.removeItemsFromLists(viewModel.inputParts.keys.elementAt(index))
                    }, modifier = Modifier.weight(0.5f)) {
                        Icon(
                            Icons.Default.RemoveCircle,
                            stringResource(R.string.placeholder_delete_job),
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCarSignupScreen() {
    VehicleSignupScreen(rememberNavController(), true)
}