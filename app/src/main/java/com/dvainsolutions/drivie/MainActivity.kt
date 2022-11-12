package com.dvainsolutions.drivie

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.navigation.Screen
import com.dvainsolutions.drivie.navigation.SetupNavGraph
import com.dvainsolutions.drivie.ui.theme.DrivieTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrivieTheme {
                val appState = rememberAppState()

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(
                            hostState = it,
                            modifier = Modifier.padding(8.dp),
                            snackbar = { snackbarData ->
                                Snackbar(
                                    snackbarData,
                                    contentColor = MaterialTheme.colors.onPrimary
                                )
                            }
                        )
                    },
                    scaffoldState = appState.scaffoldState
                ) {
                    val startDestination =
                        if (Firebase.auth.currentUser != null)
                            Screen.HomeScreen.route
                        else
                            Screen.LoginScreen.route

                    SetupNavGraph(
                        navController = rememberNavController(),
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, snackbarManager, resources, coroutineScope) {
    DrivieAppState(scaffoldState, snackbarManager, resources, coroutineScope)
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@Composable
@ReadOnlyComposable
fun textResource(@StringRes id: Int): CharSequence =
    LocalContext.current.resources.getText(id)