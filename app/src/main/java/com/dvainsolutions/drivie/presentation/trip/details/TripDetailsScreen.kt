package com.dvainsolutions.drivie.presentation.trip.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.dvainsolutions.drivie.common.custom_composables.CustomTextField
import com.dvainsolutions.drivie.common.ext.toFormattedString
import com.dvainsolutions.drivie.navigation.Screen
import java.text.DecimalFormat

@Composable
fun TripDetailsScreen(
    navController: NavHostController,
    isComingFromTripScreen: Boolean? = false,
    tripId: String?,
    viewModel: TripDetailsViewModel = hiltViewModel()
) {
    val uiState = viewModel.tripDetailsState
    val context = LocalContext.current

    LaunchedEffect(true) {
        if (tripId != null) {
            viewModel.getTripDetails(tripId)
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(id = R.string.app_bar_title_trip_details),
                showNavigationIcon = isComingFromTripScreen == false,
                onNavigationIconClick = { navController.popBackStack() },
                showActionIcon = isComingFromTripScreen == true,
                onActionIconClick = {
                    navController.navigate(Screen.HomeScreen.route)
                }
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
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.Top
                ) {
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.data.date?.toDate()?.toFormattedString(),
                        label = { Text(text = stringResource(id = R.string.placeholder_date)) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.data.startLocation,
                        label = { Text(text = stringResource(id = R.string.placeholder_trip_start)) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.data.targetLocation,
                        label = { Text(text = stringResource(id = R.string.placeholder_trip_end)) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                         modifier = Modifier.fillMaxWidth(),
                         text = uiState.data.distance.toString(),
                         label = { Text(text = stringResource(id = R.string.placeholder_part_mileage)) },
                         enabled = false
                     )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = DecimalFormat("#.##").format(uiState.data.consumption),
                        label = { Text(text = stringResource(id = R.string.placeholder_trip_used_fuel_amount)) },
                        enabled = false
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TripDetailsScreenPreview() {
    TripDetailsScreen(navController = rememberNavController(), false, "")
}