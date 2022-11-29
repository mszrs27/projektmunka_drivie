package com.dvainsolutions.drivie.presentation.statistics.misc_data_stat.details

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.dvainsolutions.drivie.common.custom_composables.CustomTextField
import com.dvainsolutions.drivie.common.ext.toFormattedString

@Composable
fun MiscDetailsScreen(
    navController: NavHostController,
    miscId: String? = null,
    viewModel: MiscDetailsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(true) {
       if (miscId != null) {
            viewModel.getMiscDetails(miscId)
        }
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_misc_details, viewModel.miscType.getLabel(context)),
                onNavigationIconClick = { navController.popBackStack() },
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
                    if (uiState.misc?.weightTax != null) {
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.weightTax.date.toDate().toFormattedString(),
                            label = { Text(text = stringResource(id = R.string.placeholder_date)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.weightTax.price?.toString() ?: "",
                            label = { Text(text = stringResource(id = R.string.placeholder_cost)) },
                            enabled = false,
                            trailingIcon = {
                                Text(text = " Ft")
                            }
                        )
                    }
                    else if (uiState.misc?.insurance != null) {
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.insurance.date.toDate().toFormattedString(),
                            label = { Text(text = stringResource(id = R.string.placeholder_date)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.insurance.price.toString(),
                            label = { Text(text = stringResource(id = R.string.placeholder_cost)) },
                            enabled = false,
                            trailingIcon = {
                                Text(text = " Ft")
                            }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.insurance.type,
                            label = { Text(text = stringResource(id = R.string.misc_insurance_type)) },
                            enabled = false
                        )
                    } else if (uiState.misc?.vignette != null){
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.vignette.startDate.toDate().toFormattedString(),
                            label = { Text(text = stringResource(id = R.string.placeholder_start_date)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.vignette.vehicleType,
                            label = { Text(text = stringResource(id = R.string.misc_vignette_vehicle_type)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.vignette.regionalType,
                            label = { Text(text = stringResource(id = R.string.misc_vignette_regional_type)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.vignette.endDate.toDate().toFormattedString(),
                            label = { Text(text = stringResource(id = R.string.placeholder_end_date)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.misc.vignette.price.toString(),
                            label = { Text(text = stringResource(id = R.string.placeholder_cost)) },
                            enabled = false,
                            trailingIcon = {
                                Text(text = " Ft")
                            }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(id = R.string.error_no_data))
                        }
                    }
                }
            }
        }
    }
}