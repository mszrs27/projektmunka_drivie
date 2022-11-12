package com.dvainsolutions.drivie.service.impl

import android.net.Uri
import com.dvainsolutions.drivie.service.StorageService
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class StorageServiceImpl @Inject constructor() : StorageService {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    override fun uploadPicture(
        file: Uri,
        currentUserId: String,
        path: String,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable?) -> Unit
    ) {
        val pictureRef =
            storageRef.child("${path}/${currentUserId}/${file.lastPathSegment}")
        val uploadTask = pictureRef.putFile(file)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    onFailure(it)
                }
            }
            pictureRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onSuccess(downloadUri.toString())
            } else {
                onFailure(task.exception)
            }
        }
    }
}