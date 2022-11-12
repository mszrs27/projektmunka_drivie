package com.dvainsolutions.drivie.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomButton
import com.dvainsolutions.drivie.common.custom_composables.EmailField
import com.dvainsolutions.drivie.common.custom_composables.PasswordField
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.utils.NoRippleInteractionSource
import com.dvainsolutions.drivie.R.string as AppRes

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.drivie_logo),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(200.dp)
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(50.dp))
            EmailField(
                value = uiState.email,
                onNewValue = viewModel::onEmailChange
            )
            Spacer(modifier = Modifier.height(20.dp))
            PasswordField(
                value = uiState.password,
                placeholder = AppRes.placeholder_password,
                onNewValue = viewModel::onPasswordChange
            )
            Spacer(modifier = Modifier.height(30.dp))
            CustomButton(
                textResource = AppRes.btn_login,
                onClick = {
                    viewModel.onSignInClick() {
                        navController.popBackStack()
                        navController.navigate(Screen.HomeScreen.route)
                    }
                },
                isLoading = viewModel.isButtonLoading.value
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(AppRes.text_no_registration))
                TextButton(
                    onClick = {
                        navController.navigate(Screen.SignupScreen.route)
                    },
                    interactionSource = remember {
                        NoRippleInteractionSource()
                    }
                ) {
                    Text(text = stringResource(AppRes.text_registration), fontSize = 16.sp)
                }
            }
            //TODO DELETE!
            TextButton(
                onClick = {
                    navController.navigate("${Screen.VehicleSignupScreen.route}/${true}")
                },
                interactionSource = remember {
                    NoRippleInteractionSource()
                }
            ) {
                Text(text = "debug reg", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}