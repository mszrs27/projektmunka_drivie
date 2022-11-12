package com.dvainsolutions.drivie.service

import android.net.Uri

interface StorageService {
    fun uploadPicture(
        file: Uri,
        currentUserId: String,
        path: String,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable?) -> Unit
    )
}