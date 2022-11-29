package com.dvainsolutions.drivie.presentation.profile

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
import com.dvainsolutions.drivie.common.custom_composables.*
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
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
                    Text(text = stringResource(id = R.string.btn_camera))
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
                    Text(text = stringResource(id = R.string.btn_gallery))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CustomAppBar(
                    titleText = stringResource(R.string.app_bar_title_modify_profile),
                    onNavigationIconClick = { navController.popBackStack() })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(
                                if (uiState.pictureUrl?.isNotEmpty() == true && (viewModel.galleryResultUri.value == null && viewModel.cameraResultUri.value == null))
                                    uiState.pictureUrl
                                else
                                    if (viewModel.hasCameraImage) viewModel.cameraResultUri.value else viewModel.galleryResultUri.value
                            )
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_person),
                        error = painterResource(R.drawable.ic_person),
                        contentDescription = stringResource(R.string.desc_profile_picture),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(DrivieGray)
                            .clickable(
                                onClick = {
                                    coroutineScope.launch {
                                        if (bottomSheetScaffoldState.isVisible) {
                                            bottomSheetScaffoldState.hide()
                                        } else {
                                            bottomSheetScaffoldState.show()
                                        }
                                    }
                                }
                            )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = uiState.name,
                        onValueChangeFunction = viewModel::onNameChange,
                        label = { Text(text = stringResource(id = R.string.placeholder_username)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = stringResource(
                                    R.string.placeholder_username
                                )
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    EmailField(
                        value = uiState.email,
                        onNewValue = viewModel::onEmailChange,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PasswordField(
                        value = uiState.oldPassword,
                        onNewValue = viewModel::onOldPasswordChange,
                        keyboardAction = ImeAction.Next,
                        placeholder = R.string.placeholder_old_password
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PasswordField(
                        value = uiState.password,
                        onNewValue = viewModel::onPasswordChange,
                        keyboardAction = ImeAction.Next,
                        placeholder = R.string.placeholder_new_password
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PasswordField(
                        value = uiState.passwordAgain,
                        onNewValue = viewModel::onPasswordAgainChange,
                        placeholder = R.string.placeholder_repeat_password
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    if (viewModel.hasDataChanged) {
                        CustomButton(
                            textResource = R.string.btn_save_data,
                            onClick = viewModel::saveData,
                            isLoading = uiState.isLoading
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomButton(
                        textResource = R.string.btn_sign_out,
                        onClick = {
                            viewModel.logout()
                            navController.apply {
                                popBackStack()
                                navigate(Screen.LoginScreen.route)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}