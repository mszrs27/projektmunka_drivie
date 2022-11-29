package com.dvainsolutions.drivie.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dvainsolutions.drivie.presentation.home.HomeScreen
import com.dvainsolutions.drivie.presentation.login.LoginScreen
import com.dvainsolutions.drivie.presentation.other_data.OtherDataDetailsScreen
import com.dvainsolutions.drivie.presentation.profile.ProfileScreen
import com.dvainsolutions.drivie.presentation.refuel.RefuelScreen
import com.dvainsolutions.drivie.presentation.refuel.details.RefuelDetailsScreen
import com.dvainsolutions.drivie.presentation.service.ConfirmServiceScreen
import com.dvainsolutions.drivie.presentation.service.ServiceScreen
import com.dvainsolutions.drivie.presentation.service.details.ServiceDetailsScreen
import com.dvainsolutions.drivie.presentation.signup.SignupScreen
import com.dvainsolutions.drivie.presentation.signup.vehicle_signup.VehicleSignupScreen
import com.dvainsolutions.drivie.presentation.statistics.StatisticsScreen
import com.dvainsolutions.drivie.presentation.statistics.misc_data_stat.MiscDataListScreen
import com.dvainsolutions.drivie.presentation.statistics.part_stat.PartsListScreen
import com.dvainsolutions.drivie.presentation.statistics.refuel_stat.RefuelListScreen
import com.dvainsolutions.drivie.presentation.statistics.service_stat.ServiceListScreen
import com.dvainsolutions.drivie.presentation.statistics.trip_stat.TripListScreen
import com.dvainsolutions.drivie.presentation.trip.TripScreen
import com.dvainsolutions.drivie.presentation.trip.details.TripDetailsScreen
import com.dvainsolutions.drivie.presentation.vehicles.VehicleListScreen
import com.dvainsolutions.drivie.presentation.vehicles.details.VehicleDetailsScreen


@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(route = Screen.SignupScreen.route) {
            SignupScreen(navController)
        }
        composable(
            route = "${Screen.VehicleSignupScreen.route}/{isComingFromRegistration}",
            arguments = listOf(navArgument("isComingFromRegistration") { type = NavType.BoolType })
        ) {
            VehicleSignupScreen(
                navController,
                isComingFromRegistration = it.arguments?.getBoolean("isComingFromRegistration")
            )
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(navController)
        }
        composable(route = Screen.VehicleListScreen.route) {
            VehicleListScreen(navController)
        }
        composable(
            route = "${Screen.VehicleDetailsScreen.route}/{carId}",
            arguments = listOf(navArgument("carId") { type = NavType.StringType })
        ) {
            VehicleDetailsScreen(
                navController,
                carId = it.arguments?.getString("carId")
            )
        }
        composable(route = Screen.StatisticsScreen.route) {
            StatisticsScreen(navController)
        }
        composable(route = Screen.RefuelScreen.route) {
            RefuelScreen(navController)
        }
        composable(
            route = "${Screen.RefuelDetailsScreen.route}/{isComingFromRefuelScreen}/{refuelId}",
            arguments = listOf(
                navArgument("isComingFromRefuelScreen") { type = NavType.BoolType },
                navArgument("refuelId") { type = NavType.StringType })
        ) {
            RefuelDetailsScreen(
                navController,
                isComingFromRefuelScreen = it.arguments?.getBoolean("isComingFromRefuelScreen"),
                refuelId = it.arguments?.getString("refuelId")
            )
        }
        composable(route = Screen.TripScreen.route) {
            TripScreen(navController)
        }
        composable(
            route = "${Screen.TripDetailsScreen.route}/{isComingFromTripScreen}/{tripId}",
            arguments = listOf(
                navArgument("isComingFromTripScreen") { type = NavType.BoolType },
                navArgument("tripId") { type = NavType.StringType })
        ) {
            TripDetailsScreen(
                navController,
                isComingFromTripScreen = it.arguments?.getBoolean("isComingFromTripScreen"),
                tripId = it.arguments?.getString("tripId")
            )
        }
        composable(route = Screen.TripListScreen.route) {
            TripListScreen(navController)
        }
        composable(route = Screen.RefuelListScreen.route) {
            RefuelListScreen(navController)
        }
        composable(route = Screen.ServiceListScreen.route) {
            ServiceListScreen(navController)
        }
        composable(route = Screen.PartsListScreen.route) {
            PartsListScreen(navController)
        }
        composable(route = Screen.OthersListScreen.route) {
            MiscDataListScreen(navController)
        }
        composable(route = Screen.OtherDataDetailsScreen.route) {
            OtherDataDetailsScreen(navController)
        }
        composable(route = Screen.ServiceScreen.route) {
            ServiceScreen(navController)
        }
        composable(route = Screen.ConfirmServiceScreen.route) {
            ConfirmServiceScreen(navController)
        }
        composable(
            route = "${Screen.ServiceDetailsScreen.route}/{isComingFromServiceScreen}/{serviceId}",
            arguments = listOf(
                navArgument("isComingFromServiceScreen") { type = NavType.BoolType },
                navArgument("serviceId") { type = NavType.StringType })
        ) {
            ServiceDetailsScreen(
                navController,
                isComingFromServiceScreen = it.arguments?.getBoolean("isComingFromServiceScreen"),
                serviceId = it.arguments?.getString("serviceId")
            )
        }
    }
}