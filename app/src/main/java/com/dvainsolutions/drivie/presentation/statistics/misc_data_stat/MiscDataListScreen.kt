package com.dvainsolutions.drivie.presentation.statistics.misc_data_stat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
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
import com.dvainsolutions.drivie.data.model.MiscData

@Composable
fun MiscDataListScreen(
    navController: NavHostController,
    viewModel: MiscDataViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddMiscDataDialog(
            viewModel = viewModel,
            onConfirmFunction = {
                viewModel.saveMiscData(onResult = {
                    showDialog = false
                })
            },
            onDismissFunction = { showDialog = false}
        )
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
        if (viewModel.uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            if (viewModel.miscDataList.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(id = R.string.error_no_data))
                }
            } else {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(vertical = 16.dp, horizontal = 24.dp),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.Top
                ) {
                    items(items = viewModel.miscDataList) { misc ->
                        CardListItem(title = getMiscTitle(misc), onClick = {
                            //navController.navigate(Screen.OtherDataDetailsScreen.route)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun getMiscTitle(data: MiscData): String {
    return if (data.insurance != null) {
        data.insurance.type
    } else if (data.vignette != null) {
        data.vignette.regionalType
    } else {
        stringResource(id = R.string.misc_type_weight_tax)
    }
}

@Preview
@Composable
fun MiscListScreenPreview() {
    MiscDataListScreen(navController = rememberNavController())
}