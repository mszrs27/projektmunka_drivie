package com.dvainsolutions.drivie.presentation.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dvainsolutions.drivie.service.FirestoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestoreService: FirestoreService
): ViewModel() {

    var userName = mutableStateOf<String?>("")
        private set

    init {
        getCurrentUserName()
    }

    private fun getCurrentUserName() {
        firestoreService.getCurrentUserDocument(onResult = {
            userName.value = it.getString("name")
        }, onError = {

        })
    }
}