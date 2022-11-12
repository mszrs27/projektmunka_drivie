package com.dvainsolutions.drivie.presentation.profile

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.custom_composables.*
import com.dvainsolutions.drivie.ui.theme.DrivieGray
import com.dvainsolutions.drivie.utils.CustomFileProvider
import com.dvainsolutions.drivie.utils.rememberGetContentActivityResult
import com.dvainsolutions.drivie.utils.rememberTakePictureActivityResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController
) {
    val bottomSheetScaffoldState = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val cameraResultUri = remember { mutableStateOf<Uri?>(null) }
    var hasCameraImage by remember {
        mutableStateOf(false)
    }
    //val galleryContent = rememberGetContentActivityResult()
    val cameraContent = rememberTakePictureActivityResult(onResult = {
        hasCameraImage = it
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
                        coroutineScope.launch {
                            bottomSheetScaffoldState.hide()
                        }
                        hasCameraImage = false
                        //galleryContent.uri = null
                        val uri = CustomFileProvider.getImageUri(context)
                        cameraResultUri.value = uri
                        cameraContent.launch(uri)
                    }
                ) {
                    Text(text = "Kamera")
                }
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetScaffoldState.hide()
                        }
                        hasCameraImage = false
                        //galleryContent.launch("image/*")
                    }
                ) {
                    Text(text = "Galéria")
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CustomAppBar(
                    titleText = "Profil módosítása",
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
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(if (hasCameraImage) cameraResultUri.value else "galleryContent.uri")
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
                    text = "",
                    placeholder = { Text(text = stringResource(id = R.string.placeholder_username)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person, contentDescription = stringResource(
                                R.string.placeholder_username
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                )
                Spacer(modifier = Modifier.height(20.dp))
                EmailField(value = "", onNewValue = { })
                Spacer(modifier = Modifier.height(20.dp))
                PasswordField(
                    value = "",
                    placeholder = R.string.placeholder_password,
                    onNewValue = { },
                    keyboardAction = ImeAction.Next
                )
                Spacer(modifier = Modifier.height(20.dp))
                PasswordField(
                    value = "",
                    placeholder = R.string.placeholder_repeat_password,
                    onNewValue = { }
                )
                Spacer(modifier = Modifier.height(30.dp))
                //TODO only show button if something was modified
                //SaveUserDataButton()
                CustomButton(
                    textResource = R.string.btn_save_data,
                    onClick = {
                        //TODO
                    }
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}