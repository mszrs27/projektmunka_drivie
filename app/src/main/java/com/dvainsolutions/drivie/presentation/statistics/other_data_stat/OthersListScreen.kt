package com.dvainsolutions.drivie.presentation.statistics.other_data_stat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.dvainsolutions.drivie.common.custom_composables.CustomDialog
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.ui.theme.DrivieGrayish
import com.dvainsolutions.drivie.ui.theme.poppinsFont

@Composable
fun OthersListScreen(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CustomDialog(title = "Alma") {

        }
    }
    val list = remember {
        listOf("Matrica 1", "Biztosítás 1", "Matrica 2", "Valami 1", "Valami 2")
    }

    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_others),
                onNavigationIconClick = {
                    navController.popBackStack()
                },
                showActionIcon = true,
                actionIcon = Icons.Default.Add,
                actionIconDescription = stringResource(R.string.desc_add_item),
                onActionIconClick = {
                    showDialog = true
                }
            )
        }
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            reverseLayout = true,
            verticalArrangement = Arrangement.Top
        ) {
            items(items = list) {
                OtherCard(it, onClick = {
                    //navController.navigate(Screen.OtherDataDetailsScreen.route)
                })
            }
        }
    }
}

@Composable
fun OtherCard(vehicle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 12.dp)
            .clickable {
                onClick.invoke()
            },
        shape = RoundedCornerShape(10.dp),
        backgroundColor = DrivieGrayish,
        elevation = 5.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = vehicle,
                style = TextStyle(
                    fontFamily = poppinsFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500
                )
            )
        }
    }
}

@Preview
@Composable
fun OthersListScreenPreview() {
    OthersListScreen(navController = rememberNavController())
}