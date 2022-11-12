package com.dvainsolutions.drivie.presentation.statistics.refuel_stat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.dvainsolutions.drivie.common.ext.toFormattedString
import com.dvainsolutions.drivie.navigation.Screen

@Composable
fun RefuelListScreen(
    navController: NavHostController,
    viewModel: RefuelListViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_refuels),
                onNavigationIconClick = { navController.popBackStack() }
            )
        }
    ) {
        if (viewModel.isLoading) {
            CircularProgressIndicator()
        } else {
            if (viewModel.refuelings.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(id = R.string.error_no_data))
                }
            }
            else {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(vertical = 16.dp, horizontal = 24.dp)
                ) {
                    items(items = viewModel.refuelings) {
                        CardListItem(title = it.date?.toDate()?.toFormattedString() ?: it.id, onClick = {
                            navController.navigate("${Screen.RefuelDetailsScreen.route}/${false}/${it.id}")
                        })
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun RefuelListScreenPreview() {
    RefuelListScreen(navController = rememberNavController())
}