package com.dvainsolutions.drivie.presentation.vehicles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.dvainsolutions.drivie.common.custom_composables.CustomButton
import com.dvainsolutions.drivie.data.model.Vehicle
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.ui.theme.DrivieGray
import com.dvainsolutions.drivie.ui.theme.poppinsFont

@Composable
fun VehicleListScreen(
    navController: NavHostController,
    viewModel: VehicleListViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_vehicles),
                onNavigationIconClick = { navController.popBackStack() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.vehicleListState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(Modifier.weight(1f)) {
                    items(items = viewModel.vehicleListState.vehicles) {
                        VehicleCard(it, onClick = {
                            viewModel.saveCarIdToDataStore(it.id)
                            navController.navigate("${Screen.VehicleDetailsScreen.route}/${it.id}")
                        })
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                CustomButton(
                    modifier = Modifier.weight(0.1f),
                    textResource = R.string.btn_car_sign_up,
                    onClick = {
                        navController.navigate("${Screen.VehicleSignupScreen.route}/${false}")
                    },
                )
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle, onClick: () -> Unit) {
    val isLoading = remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 12.dp)
            .clickable {
                onClick.invoke()
            },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = DrivieGray,
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row() {
                Column() {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(vehicle.pictureUrl)
                                .crossfade(true)
                                .build(),
                            modifier = Modifier
                                .size(70.dp)
                                .border(BorderStroke(1.dp, Color.LightGray))
                                .background(DrivieGray),
                            error = painterResource(R.drawable.ic_car),
                            onLoading = {
                                isLoading.value = true
                            },
                            onSuccess = {
                                isLoading.value = false
                            },
                            onError = {
                                isLoading.value = false
                            },
                            contentDescription = stringResource(R.string.desc_vehicle_picture),
                            contentScale = ContentScale.Crop,
                        )
                        if (isLoading.value) CircularProgressIndicator()
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column() {
                    Text(
                        text = vehicle.nickname ?: vehicle.model,
                        style = TextStyle(
                            fontFamily = poppinsFont,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W500
                        )
                    )
                    Text(
                        text = vehicle.licence,
                        style = TextStyle(
                            fontFamily = poppinsFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun VehicleScreenPreview() {
    VehicleListScreen(rememberNavController())
}