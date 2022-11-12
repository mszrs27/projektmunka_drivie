package com.dvainsolutions.drivie.utils

import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class GetContentActivityResult(
    private val launcher: ManagedActivityResultLauncher<String, Uri?>,
) {
    fun launch(mimeType: String) {
        launcher.launch(mimeType)
    }
}

class TakePictureActivityResult(
    private val launcher: ManagedActivityResultLauncher<Uri, Boolean>
) {
    fun launch(uri: Uri) {
        launcher.launch(uri)
    }
}

@Composable
fun rememberGetContentActivityResult(onResult: (uri: Uri?) -> Unit): GetContentActivityResult {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent(), onResult = {
            //uri = it
            Log.e("alma", "rememberGetContentActivityResult: ${it?.lastPathSegment}", )
            onResult(it)
        })
    return remember(launcher) {
        GetContentActivityResult(launcher)
    }
}

@Composable
fun rememberTakePictureActivityResult(
    onResult: (hasImage: Boolean) -> Unit
): TakePictureActivityResult {
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            onResult.invoke(it)
        })
    return remember(cameraLauncher) {
        TakePictureActivityResult(cameraLauncher)
    }
}

