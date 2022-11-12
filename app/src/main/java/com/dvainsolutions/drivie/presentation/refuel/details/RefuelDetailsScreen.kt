package com.dvainsolutions.drivie.presentation.refuel.details

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

@Composable
fun RefuelDetailsScreen(
    navController: NavHostController,
    isComingFromRefuelScreen: Boolean? = false,
    refuelId: String?,
    viewModel: RefuelDetailsViewModel = hiltViewModel()
) {
    val uiState = viewModel.refuelDetailsState
    val context = LocalContext.current

    LaunchedEffect(true) {
        if (refuelId != null) {
            viewModel.getRefuelDetails(refuelId)
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_refuel_details),
                showNavigationIcon = isComingFromRefuelScreen == false,
                onNavigationIconClick = { navController.popBackStack() },
                showActionIcon = isComingFromRefuelScreen == true,
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
                        text = uiState.data.location,
                        label = { Text(text = stringResource(id = R.string.placeholder_refuel_place)) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.data.quantity?.toString() ?: "",
                        label = { Text(text = stringResource(id = R.string.placeholder_refuel_fuel_amount)) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.data.type?.getLabel(context),
                        label = { Text(text = stringResource(id = R.string.placeholder_refuel_fuel_type)) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.data.mileage?.toString() ?: "",
                        label = { Text(text = stringResource(id = R.string.placeholder_mileage)) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.data.cost?.toString() ?: "",
                        label = { Text(text = stringResource(id = R.string.placeholder_refuel_total_cost)) },
                        enabled = false
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RefuelDetailsScreenPreview() {
    RefuelDetailsScreen(rememberNavController(), refuelId = "")
}