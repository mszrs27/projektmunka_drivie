package com.dvainsolutions.drivie.presentation.service

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.dvainsolutions.drivie.common.custom_composables.CustomButton
import com.dvainsolutions.drivie.navigation.Screen

@Composable
fun ConfirmServiceScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_service_confirm),
                onNavigationIconClick = { navController.popBackStack() })
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
            .padding(horizontal = 16.dp, vertical = 24.dp)) {
            LazyColumn {
                item {
                    Text("Alma")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                CustomButton(modifier = Modifier.weight(1f), textResource = R.string.btn_cancel, onClick = {
                    navController.popBackStack()
                })
                Spacer(modifier = Modifier.width(30.dp))
                CustomButton(modifier = Modifier.weight(1f), textResource = R.string.btn_confirm, onClick = {
                    navController.navigate(Screen.ServiceDetailsScreen.route)
                })
            }
        }
    }
}

@Preview
@Composable
fun ConfirmServiceScreenPreview() {
    ConfirmServiceScreen(rememberNavController())
}