package com.dvainsolutions.drivie.presentation.statistics.part_stat

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.github.mikephil.charting.charts.BarChart

@Composable
fun PartsListScreen(
    navController: NavHostController,
    viewModel: PartsViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        VehiclePartDataDialog(
            viewModel = viewModel,
            onDismissFunction = {
                showDialog.value = false
            }
        )
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_parts_list),
                onNavigationIconClick = { navController.popBackStack() })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.isLoading) CircularProgressIndicator()
            if (viewModel.partList.isNotEmpty() && !viewModel.isLoading) {
                Text(stringResource(R.string.vehicle_parts_chart_title))
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    factory = { context ->
                        BarChart(context)
                    },
                    update = { barChart ->
                        viewModel.createChartData(
                            barChart,
                            onShowDialog = { showDialog.value = true },
                            onDismissDialog = { showDialog.value = false }
                        )
                    }
                )
            } else {
                Text(text = stringResource(R.string.error_no_data))
            }
        }
    }
}

@Preview
@Composable
fun PartsListScreenPreview() {
    PartsListScreen(navController = rememberNavController())
}