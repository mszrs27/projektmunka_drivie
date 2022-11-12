package com.dvainsolutions.drivie.presentation.trip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.dvainsolutions.drivie.navigation.Screen

@Composable
fun TripScreen(
    navController: NavHostController,
    viewModel: TripViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value

    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_record_trip),
                onNavigationIconClick = { navController.popBackStack() }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                if (viewModel.vehicleList.isNotEmpty())
                    CustomSpinner(
                        dataList = viewModel.vehicleList.keys.toList(),
                        onSelected = viewModel::onVehicleChange
                    )
                else
                    CircularProgressIndicator()
                Spacer(modifier = Modifier.height(20.dp))
                DateTimePicker(
                    time = uiState.date,
                    placeholderRes = R.string.placeholder_date,
                    onTimeChange = viewModel::onDateChange,
                    placeholderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = uiState.startLocation,
                    placeholder = { Text(text = stringResource(id = R.string.placeholder_trip_start)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    onValueChangeFunction = { value ->
                        viewModel.onLocationChange(value, true)
                    }
                )
                if (viewModel.isStartLoc) {
                    LazyColumn(Modifier.fillMaxWidth()) {
                        items(items = viewModel.locations) { address ->
                            TextButton(onClick = {
                                viewModel.apply {
                                    createLocationsWithLatLong(true, address)
                                    selectAddress(address.getAddressLine(0), true)
                                }
                            }) {
                                Text(text = address.getAddressLine(0))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = uiState.targetLocation,
                    placeholder = { Text(text = stringResource(id = R.string.placeholder_trip_end)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    onValueChangeFunction = { value ->
                        viewModel.onLocationChange(value, false)
                    }
                )
                if (viewModel.isTargetLoc) {
                    LazyColumn(Modifier.fillMaxWidth()) {
                        items(items = viewModel.locations) { address ->
                            TextButton(onClick = {
                                viewModel.apply {
                                    createLocationsWithLatLong(false, address)
                                    selectAddress(address.getAddressLine(0), false)
                                }
                            }) {
                                Text(text = address.getAddressLine(0))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = uiState.mileage,
                    placeholder = { Text(text = stringResource(id = R.string.placeholder_mileage)) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChangeFunction = viewModel::onMileageChange
                )
            }
            CustomButton(
                textResource = R.string.btn_save_trip,
                onClick = {
                    viewModel.saveTripData { tripId ->
                        navController.popBackStack()
                        navController.navigate("${Screen.TripDetailsScreen.route}/${true}/${tripId}")
                    }
                },
                isLoading = viewModel.isButtonLoading.value
            )
        }
    }
}

@Preview
@Composable
fun TripScreenPreview() {
    TripScreen(navController = rememberNavController())
}