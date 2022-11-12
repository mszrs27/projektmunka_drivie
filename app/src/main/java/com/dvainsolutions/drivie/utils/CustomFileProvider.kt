package com.dvainsolutions.drivie.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.dvainsolutions.drivie.R
import java.io.File

class CustomFileProvider : FileProvider(
    R.xml.file_paths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.filesDir, "user_pics")
            directory.mkdirs()

            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory
            )
            val authority = context.packageName

            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}