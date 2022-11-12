package com.dvainsolutions.drivie.presentation.other_data

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomAppBar
import com.dvainsolutions.drivie.common.custom_composables.CustomTextField

@Composable
fun OtherDataDetailsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CustomAppBar(
                titleText = stringResource(R.string.app_bar_title_others),
                onNavigationIconClick = {
                    navController.popBackStack()
                },
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DataCard() {
    val keyboardController = LocalSoftwareKeyboardController.current

    var jobText by remember { mutableStateOf("") }
    var jobCost by remember { mutableStateOf("") }
    val jobTextList = remember {
        mutableStateListOf<String>()
    }
    val jobCostList = remember {
        mutableStateListOf<String>()
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        CustomTextField(
            modifier = Modifier.weight(1f),
            text = jobText,
            onValueChangeFunction = { jobText = it },
            label = { Text(text = stringResource(id = R.string.placeholder_service_name)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.width(15.dp))
        CustomTextField(
            modifier = Modifier.weight(1f),
            text = jobCost,
            onValueChangeFunction = {
                if (it.isDigitsOnly())
                    jobCost = it
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
                dynamicColumnHeight += 40.dp
                keyboardController?.hide()
                jobTextList.add(jobText)
                jobCostList.add(jobCost)
                jobText = ""
                jobCost = ""
            }
        ) {
            Text("Munka hozzáadása")
        }
        LazyColumn(modifier = Modifier.height(dynamicColumnHeight)) {
            items(jobTextList.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${jobTextList[index]} - ${jobCostList[index]} Ft",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        dynamicColumnHeight -= 40.dp
                        jobTextList.remove(jobTextList[index])
                        jobCostList.remove(jobCostList[index])
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
fun OtherDataScreenPreview() {
    OtherDataDetailsScreen(rememberNavController())
}