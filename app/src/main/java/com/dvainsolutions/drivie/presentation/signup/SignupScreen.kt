package com.dvainsolutions.drivie.presentation.signup

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.CustomButton
import com.dvainsolutions.drivie.common.custom_composables.CustomTextField
import com.dvainsolutions.drivie.common.custom_composables.EmailField
import com.dvainsolutions.drivie.common.custom_composables.PasswordField
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarMessage
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.ui.theme.DrivieGray
import com.dvainsolutions.drivie.utils.CustomFileProvider
import com.dvainsolutions.drivie.utils.UiText
import com.dvainsolutions.drivie.utils.rememberGetContentActivityResult
import com.dvainsolutions.drivie.utils.rememberTakePictureActivityResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import com.dvainsolutions.drivie.R.string as AppRes

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: SignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    val bottomSheetScaffoldState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val permissionsState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val galleryContent = rememberGetContentActivityResult(onResult = {
        viewModel.galleryResultUri.value = it
    })
    val cameraContent = rememberTakePictureActivityResult(onResult = {
        viewModel.hasCameraImage = it
    })

    BackHandler(
        onBack = {
            coroutineScope.launch {
                if (bottomSheetScaffoldState.isVisible) {
                    bottomSheetScaffoldState.hide()
                } else {
                    navController.popBackStack()
                }
            }
        }
    )

    ModalBottomSheetLayout(
        sheetState = bottomSheetScaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(DrivieGray),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextButton(
                    onClick = {
                        if (permissionsState.status.isGranted) {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.hide()
                            }
                            viewModel.hasCameraImage = false
                            viewModel.galleryResultUri.value = null
                            val uri = CustomFileProvider.getImageUri(context)
                            viewModel.cameraResultUri.value = uri
                            cameraContent.launch(uri)
                        } else {
                            SnackbarManager.showMessage(
                                SnackbarMessage.StringSnackbar(
                                    UiText.StringResource(resId = R.string.permission_msg_camera)
                                        .asString(context)
                                )
                            )
                        }
                    }
                ) {
                    Text(text = "Kamera")
                }
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.hide()
                        }
                        viewModel.hasCameraImage = false
                        galleryContent.launch("image/*")
                    }
                ) {
                    Text(text = "Galéria")
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(if (viewModel.hasCameraImage) viewModel.cameraResultUri.value else viewModel.galleryResultUri.value)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.add_profile_pic),
                error = painterResource(R.drawable.add_profile_pic),
                contentDescription = stringResource(R.string.desc_profile_picture),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(DrivieGray.copy(alpha = 0.2f))
                    .clickable(onClick = {
                        coroutineScope.launch {
                            permissionsState.launchPermissionRequest()
                            if (bottomSheetScaffoldState.isVisible) {
                                bottomSheetScaffoldState.hide()
                            } else {
                                bottomSheetScaffoldState.show()
                            }
                        }
                    })
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                text = uiState.userName,
                placeholder = { Text(text = stringResource(id = AppRes.placeholder_username)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person, contentDescription = stringResource(
                            AppRes.placeholder_username
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                onValueChangeFunction = viewModel::onUsernameChange
            )
            Spacer(modifier = Modifier.height(20.dp))
            EmailField(
                value = uiState.email,
                onNewValue = viewModel::onEmailChange
            )
            Spacer(modifier = Modifier.height(20.dp))
            PasswordField(
                value = uiState.password,
                placeholder = R.string.placeholder_password,
                onNewValue = viewModel::onPasswordChange,
                keyboardAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(20.dp))
            PasswordField(
                value = uiState.repeatPassword,
                placeholder = AppRes.placeholder_repeat_password,
                onNewValue = viewModel::onRepeatPasswordChange
            )
            Spacer(modifier = Modifier.height(30.dp))
            CustomButton(
                textResource = AppRes.btn_sign_up,
                onClick = {
                    viewModel.onSignUpClick() {
                        navController.popBackStack()
                        navController.navigate("${Screen.VehicleSignupScreen.route}/${true}")
                    }
                },
                isLoading = viewModel.isButtonLoading.value
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    SignupScreen(rememberNavController())
}