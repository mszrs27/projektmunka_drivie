package com.dvainsolutions.drivie.utils

import android.content.Context
import android.net.Uri
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import javax.inject.Inject

class TextRecognition @Inject constructor(private val context: Context) {

    fun runTextRecognition(inputImage: Uri, onProcessResult: (List<Text.TextBlock>) -> Unit) {
        try {
            val image = InputImage.fromFilePath(context, inputImage)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    processTextRecognitionResult(result, onProcessResult = onProcessResult)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
        catch (e: IOException) {
            SnackbarManager.showMessage(R.string.error_text_recognition)
        }
    }

    private fun processTextRecognitionResult(result: Text, onProcessResult: (List<Text.TextBlock>) -> Unit) {
        val listOfBlocks: MutableList<Text.TextBlock> = mutableListOf()
        for (block in result.textBlocks) {
            listOfBlocks.add(block)
        }
        onProcessResult.invoke(listOfBlocks)
    }
}