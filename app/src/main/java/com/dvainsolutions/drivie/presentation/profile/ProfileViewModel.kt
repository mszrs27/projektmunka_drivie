package com.dvainsolutions.drivie.presentation.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.ext.passwordMatches
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager.onError
import com.dvainsolutions.drivie.data.model.User
import com.dvainsolutions.drivie.service.AccountService
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.service.StorageService
import com.dvainsolutions.drivie.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val firestoreService: FirestoreService
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
    private var userData = User()

    private val pictureUrl get() = uiState.pictureUrl
    private val name get() = uiState.name
    private val email get() = uiState.email
    private val newPassword get() = uiState.password
    private val oldPassword get() = uiState.oldPassword
    var hasDataChanged by mutableStateOf(false)

    var hasCameraImage by mutableStateOf(false)
    val cameraResultUri = mutableStateOf<Uri?>(null)
    val galleryResultUri = mutableStateOf<Uri?>(null)

    init {
        getUserData()
    }

    fun onNameChange(value: String) {
        hasDataChanged = value != userData.name
        uiState = uiState.copy(name = value)
    }

    fun onEmailChange(value: String) {
        hasDataChanged = value != userData.email
        uiState = uiState.copy(email = value)
    }

    fun onOldPasswordChange(value: String) {
        uiState = uiState.copy(oldPassword = value)
    }

    fun onPasswordChange(value: String) {
        hasDataChanged = value.isNotEmpty()
        uiState = uiState.copy(password = value)
    }

    fun onPasswordAgainChange(value: String) {
        hasDataChanged = value.isNotEmpty()
        uiState = uiState.copy(passwordAgain = value)
    }

    fun saveData() {
        if (!hasDataChanged) return
        if (newPassword.isNotEmpty() && !newPassword.passwordMatches(uiState.passwordAgain)) {
            SnackbarManager.showMessage(R.string.error_password_match)
            return
        } else if (uiState.passwordAgain.isNotEmpty() && newPassword.isEmpty()) {
            SnackbarManager.showMessage(R.string.error_password_match)
            return
        } else if (oldPassword.isEmpty() && (newPassword.isNotEmpty() || uiState.passwordAgain.isNotEmpty())) {
            SnackbarManager.showMessage(R.string.error_no_password)
            return
        }

        if (name != userData.name || pictureUrl != userData.pictureUrl) {
            val currUserId = accountService.getCurrentUser()?.uid!!
            if (!name.isNullOrEmpty()) {
                uiState = uiState.copy(isLoading = true)
                firestoreService.updateUser(
                    userId = currUserId,
                    value = hashMapOf(
                        "name" to name!!,
                    ),
                    onResult = {
                        if (it == null) {
                            if (cameraResultUri.value != null || galleryResultUri.value != null) {
                                updateProfilePicture(
                                    currUserId,
                                    onResult = {
                                        uiState = uiState.copy(isLoading = false)
                                        getUserData()
                                    })
                            } else {
                                uiState = uiState.copy(isLoading = false)
                            }
                        } else {
                            onError(it)
                        }
                    }
                )
            } else if (cameraResultUri.value != null || galleryResultUri.value != null) {
                uiState = uiState.copy(isLoading = true)
                updateProfilePicture(currUserId, onResult = {
                    uiState = uiState.copy(isLoading = false)
                    getUserData()
                })
            }
        }

        if (oldPassword.isNotEmpty() && (newPassword.isNotEmpty() || email?.isNotEmpty() == true)) {
            uiState = uiState.copy(isLoading = true)
            accountService.changeUserData(
                newEmail = email,
                newPassword = newPassword,
                oldPassword = oldPassword,
                onResult = {
                    uiState = uiState.copy(isLoading = false)
                },
                onError = {
                    onError(it)
                    uiState = uiState.copy(isLoading = false)
                }
            )
        }
    }

    private fun updateProfilePicture(currUserId: String, onResult: () -> Unit) {
        (if (hasCameraImage) cameraResultUri.value else galleryResultUri.value)?.let { uri ->
            storageService.uploadPicture(
                file = uri,
                currentUserId = currUserId,
                path = Constants.PROFILE_FOLDER,
                onFailure = { err ->
                    err?.let { it -> onError(it) }
                },
                onSuccess = {
                    firestoreService.updateUser(
                        userId = currUserId,
                        value = hashMapOf("pictureUrl" to it)
                    ) { error ->
                        if (error == null) {
                            onResult.invoke()
                        } else {
                            onError(error)
                        }
                    }
                }
            )
        }
    }

    private fun getUserData() {
        uiState = uiState.copy(isLoading = true, oldPassword = "", password = "", passwordAgain = "")
        firestoreService.getCurrentUserData(
            onResult = {
                userData = it
                uiState = uiState.copy(
                    pictureUrl = it.pictureUrl,
                    name = it.name,
                    email = it.email
                )
                uiState = uiState.copy(isLoading = false)
            },
            onError = { err ->
                uiState = uiState.copy(isLoading = false)
                err?.let { onError(it) }
            }
        )
    }

    fun logout() {
        accountService.logoutUser()
    }
}