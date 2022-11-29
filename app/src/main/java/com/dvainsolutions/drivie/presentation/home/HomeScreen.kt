package com.dvainsolutions.drivie.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.ui.theme.DrivieDarkBlue
import com.dvainsolutions.drivie.ui.theme.DrivieGray
import com.dvainsolutions.drivie.R.string as AppRes

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {
                        navController.navigate(Screen.ProfileScreen.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = stringResource(AppRes.desc_user_profile),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        viewModel.userName.value?.let { name ->
                            if (name.isNotEmpty()) {
                                Text(
                                    text = "Szia, ${name}!",
                                    style = MaterialTheme.typography.h2,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        CarAnimationLoader()
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        DrivieGray,
                        shape = RoundedCornerShape(
                            CornerSize(50.dp),
                            CornerSize(50.dp),
                            CornerSize(0.dp),
                            CornerSize(0.dp)
                        )
                    )
                    .weight(0.7f)
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OptionCard(
                            textResource = AppRes.card_cars,
                            onClick = { navController.navigate(Screen.VehicleListScreen.route) })
                        OptionCard(
                            textResource = AppRes.card_tanking,
                            onClick = { navController.navigate(Screen.RefuelScreen.route) })
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OptionCard(
                            textResource = AppRes.card_service,
                            onClick = { navController.navigate(Screen.ServiceScreen.route) })
                        OptionCard(
                            textResource = AppRes.card_trip,
                            onClick = { navController.navigate(Screen.TripScreen.route) })
                    }
                }
            }
        }
    }
}

@Composable
fun OptionCard(textResource: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(90.dp)
            .clickable {
                onClick.invoke()
            },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = DrivieDarkBlue,
        elevation = 5.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(textResource),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun CarAnimationLoader() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.car_animation))

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        restartOnPlay = true,
        cancellationBehavior = LottieCancellationBehavior.Immediately,
    )

    LottieAnimation(composition, { progress })
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}