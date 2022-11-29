package com.dvainsolutions.drivie.presentation.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.dvainsolutions.drivie.common.custom_composables.CustomButton
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.ui.theme.*

@Composable
fun StatisticsScreen(
    navController: NavHostController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        CustomAppBar(
            titleText = stringResource(R.string.app_bar_title_statistics),
            onNavigationIconClick = { navController.popBackStack() })
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticCard(
                    statName = stringResource(R.string.avg_title_consumption),
                    data = viewModel.avgData.let { data ->
                        if (data.isNotEmpty()){
                            "${data.first()} L"
                        }
                        else ""
                    },
                    cardColor = DrivieDarkBlue,
                    isLoading = viewModel.isLoading
                )
                Spacer(modifier = Modifier.width(20.dp))
                StatisticCard(
                    statName = stringResource(R.string.avg_title_refuel),
                    data = viewModel.avgData.let { data ->
                        if (data.isNotEmpty())
                            "${data[2]} L"
                        else ""
                    },
                    isLoading = viewModel.isLoading
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticCard(
                    statName = stringResource(R.string.avg_title_distance),
                    data = viewModel.avgData.let { data ->
                        if (data.isNotEmpty())
                            "${data[1]} km"
                        else ""
                    },
                    cardColor = DrivieLightBlue,
                    isLoading = viewModel.isLoading
                )
                Spacer(modifier = Modifier.width(20.dp))
                StatisticCard(
                    statName = stringResource(R.string.avg_title_refuel_cost),
                    data = viewModel.avgData.let { data ->
                        if (data.isNotEmpty())
                            "${data.last()} Ft"
                        else ""
                    },
                    cardColor = DrivieGrayish,
                    isLoading = viewModel.isLoading
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.8f),
                verticalArrangement = Arrangement.Bottom
            ) {
                CustomButton(
                    textResource = R.string.btn_vehicle_refuel,
                    onClick = {
                        navController.navigate(Screen.RefuelListScreen.route)
                    }
                )
                CustomButton(
                    modifier = Modifier.padding(top = 10.dp),
                    textResource = R.string.btn_vehicle_trips,
                    onClick = {
                        navController.navigate(Screen.TripListScreen.route)
                    }
                )
                CustomButton(
                    modifier = Modifier.padding(top = 10.dp),
                    textResource = R.string.btn_vehicle_services,
                    onClick = {
                        navController.navigate(Screen.ServiceListScreen.route)
                    }
                )
                CustomButton(
                    modifier = Modifier.padding(top = 10.dp),
                    textResource = R.string.btn_vehicle_parts,
                    onClick = {
                        navController.navigate(Screen.PartsListScreen.route)
                    }
                )
                CustomButton(
                    modifier = Modifier.padding(top = 10.dp),
                    textResource = R.string.btn_vehicle_other,
                    onClick = {
                        navController.navigate(Screen.MiscDataListScreen.route)
                    }
                )
            }
        }
    }
}

@Composable
fun StatisticCard(
    statName: String,
    data: String,
    cardColor: Color = DrivieGray,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .height(120.dp)
            .width(130.dp)
            .padding(top = 12.dp),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = cardColor,
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statName, textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = poppinsFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500
                )
            )
            if (isLoading)
                CircularProgressIndicator()
            else
                Text(
                    text = data,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontFamily = poppinsFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500
                    )
                )
        }
    }
}

@Preview
@Composable
fun StatisticsScreenPreview() {
    StatisticsScreen(rememberNavController())
}