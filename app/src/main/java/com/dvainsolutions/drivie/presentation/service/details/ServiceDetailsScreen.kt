package com.dvainsolutions.drivie.presentation.service.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CardListItem
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.dvainsolutions.drivie.common.custom_composables.CustomTextField
import com.dvainsolutions.drivie.common.ext.toFormattedString
import com.dvainsolutions.drivie.navigation.Screen

@Composable
fun ServiceDetailsScreen(
    navController: NavHostController,
    isComingFromServiceScreen: Boolean? = false,
    serviceId: String?,
    viewModel: ServiceDetailsViewModel = hiltViewModel()
) {
    val state = viewModel.serviceDetailsState

    LaunchedEffect(true) {
        viewModel.getDetails(serviceId)
    }

    //TODO layout
    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_service_details),
                showNavigationIcon = isComingFromServiceScreen == false,
                onNavigationIconClick = { navController.popBackStack() },
                showActionIcon = isComingFromServiceScreen == true,
                onActionIconClick = {
                    navController.navigate(Screen.HomeScreen.route)
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 24.dp),
        ) {
            item {
                Column(Modifier.fillMaxSize()) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    }
                    else {
                        CustomTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = state.data.vehicle + serviceId,
                            label = { Text(text = stringResource(id = R.string.placeholder_vehicle)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = state.data.date?.toDate()?.toFormattedString(),
                            label = { Text(text = stringResource(id = R.string.placeholder_date)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        CustomTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = state.data.mileage?.toString() ?: "",
                            label = { Text(text = stringResource(id = R.string.placeholder_mileage)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        if (state.data.jobsDone.isNotEmpty()) {
                            Text(text = stringResource(R.string.list_title_jobs), style = MaterialTheme.typography.h6)
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .heightIn(max = 400.dp)
                            ) {
                                items(state.data.jobsDone.keys.toList()) { jobName ->
                                    CardListItem(
                                        modifier = Modifier.padding(bottom = 5.dp),
                                        title = jobName,
                                        additionalContent = true,
                                        additionalText = "${state.data.jobsDone[jobName].toString()} Ft"
                                    )
                                }
                            }
                        }
                        if (state.data.replacedParts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(text = stringResource(R.string.list_title_parts), style = MaterialTheme.typography.h6)
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyColumn(
                                modifier = Modifier
                                    .heightIn(max = 400.dp)
                            ) {
                                items(state.data.replacedParts.keys.toList()) { partName ->
                                    CardListItem(
                                        modifier = Modifier.padding(5.dp),
                                        title = partName,
                                        additionalContent = true,
                                        additionalText = "${state.data.replacedParts[partName].toString()} Ft"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ServiceDetailsScreenPreview() {
    ServiceDetailsScreen(navController = rememberNavController(), false, "")
}