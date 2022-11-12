package com.dvainsolutions.drivie.presentation.service

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.*
import com.dvainsolutions.drivie.data.model.VehiclePartList

@Composable
fun ServiceScreen(
    navController: NavHostController,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_service),
                onNavigationIconClick = { navController.popBackStack() })
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    if (viewModel.vehicleList.isNotEmpty())
                        CustomSpinner(
                            dataList = viewModel.vehicleList.keys.toList(),
                            onSelected = viewModel::onVehicleChange
                        )
                    else
                        CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(20.dp))
                    DateTimePicker(
                        time = viewModel.uiState.value.date,
                        placeholderRes = R.string.placeholder_date,
                        onTimeChange = viewModel::onDateChange,
                        placeholderColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    JobsDoneSection(viewModel)
                    ReplacedPartsSection(viewModel)
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = viewModel.uiState.value.mileage,
                        label = { Text(text = stringResource(id = R.string.placeholder_mileage)) },
                        onValueChangeFunction = viewModel::onMileageChange,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomButton(
                        textResource = R.string.btn_save_data,
                        onClick = {
                            viewModel.saveServiceData {
                            }
                        },
                        isLoading = viewModel.isLoading
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun JobsDoneSection(viewModel: ServiceViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        CustomTextField(
            modifier = Modifier.weight(1f),
            text = viewModel.jobName,
            onValueChangeFunction = viewModel::setJobNameText,
            label = { Text(text = stringResource(id = R.string.placeholder_service_name)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(
            modifier = Modifier.weight(1f),
            text = viewModel.jobCost,
            onValueChangeFunction = {
                if (it.isDigitsOnly())
                   viewModel.setJobCostText(it)
            },
            label = { Text(text = stringResource(id = R.string.placeholder_cost)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                Text(text = " Ft")
            }
        )
    }
    Column(horizontalAlignment = Alignment.Start) {
        var dynamicColumnHeight by remember {
            mutableStateOf(10.dp)
        }
        TextButton(
            onClick = {
                keyboardController?.hide()
                viewModel.apply {
                    addItemsToJobsDoneList(onResult = {
                        dynamicColumnHeight += 40.dp
                    })
                }
            }
        ) {
            Text("Munka hozzáadása")
        }
        LazyColumn(modifier = Modifier.height(dynamicColumnHeight)) {
            items(viewModel.jobsDone.keys.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${viewModel.jobsDone.keys.elementAt(index)} - ${viewModel.jobsDone.values.elementAt(index)} Ft",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        dynamicColumnHeight -= 40.dp
                        viewModel.removeItemsFromJobsDoneList(viewModel.jobsDone.keys.elementAt(index))
                    }, modifier = Modifier.weight(0.5f)) {
                        Icon(
                            Icons.Default.RemoveCircle,
                            stringResource(R.string.placeholder_delete_job),
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ReplacedPartsSection(viewModel: ServiceViewModel) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        CustomSpinner(
            modifier = Modifier.weight(1f),
            dataList = VehiclePartList.parts.map { it.name },
            onSelected = {
                viewModel.setPartNameText(it)
            }
        )
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(
            modifier = Modifier.weight(1f),
            text = viewModel.partCost,
            onValueChangeFunction = {
                if (it.isDigitsOnly())
                    viewModel.setPartCostText(it)
            },
            label = { Text(text = stringResource(id = R.string.placeholder_cost)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                Text(text = " Ft")
            }
        )
    }
    Column(horizontalAlignment = Alignment.Start) {
        var dynamicColumnHeight by remember {
            mutableStateOf(10.dp)
        }
        TextButton(
            onClick = {
                viewModel.apply {
                    keyboardController?.hide()
                    addItemsToPartsList(onResult = {
                        dynamicColumnHeight += 40.dp
                    })
                }
            }
        ) {
            Text("Alkatrész hozzáadása")
        }
        LazyColumn(modifier = Modifier.height(dynamicColumnHeight)) {
            items(viewModel.replacedParts.keys.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${viewModel.replacedParts.keys.elementAt(index)} - ${(viewModel.replacedParts.values.elementAt(index))} Ft",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        dynamicColumnHeight -= 40.dp
                        viewModel.removeItemsFromPartList(viewModel.replacedParts.keys.elementAt(index))
                    }, modifier = Modifier.weight(0.5f)) {
                        Icon(
                            Icons.Default.RemoveCircle,
                            stringResource(R.string.placeholder_delete_job),
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ServiceScreenPreview() {
    ServiceScreen(navController = rememberNavController())
}