package com.dvainsolutions.drivie.presentation.signup

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvainsolutions.drivie.common.ext.isValidEmail
import com.dvainsolutions.drivie.common.ext.isValidPassword
import com.dvainsolutions.drivie.common.ext.passwordMatches
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager.onError
import com.dvainsolutions.drivie.data.model.User
import com.dvainsolutions.drivie.presentation.common_files.AuthenticationUiState
import com.dvainsolutions.drivie.service.AccountService
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.service.StorageService
import com.dvainsolutions.drivie.utils.Constants.PROFILE_FOLDER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dvainsolutions.drivie.R.string as AppStringRes

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val storageService: StorageService
) : ViewModel() {

    var uiState = mutableStateOf(AuthenticationUiState())
        private set

    private var _isButtonLoading = mutableStateOf(false)
    val isButtonLoading: State<Boolean> = _isButtonLoading

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password
    private val username get() = uiState.value.userName

    var hasCameraImage by mutableStateOf(false)
    val cameraResultUri = mutableStateOf<Uri?>(null)
    val galleryResultUri = mutableStateOf<Uri?>(null)

    fun onUsernameChange(newValue: String) {
        uiState.value = uiState.value.copy(userName = newValue)
    }

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onSignUpClick(onNavigation: () -> Unit) {
        if (_isButtonLoading.value) return
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppStringRes.error_email)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(AppStringRes.error_password)
            return
        }

        if (!password.passwordMatches(uiState.value.repeatPassword)) {
            SnackbarManager.showMessage(AppStringRes.error_password_match)
            return
        }

        viewModelScope.launch {
            _isButtonLoading.value = true
            accountService.createAccount(email, password) { error ->
                if (error == null) {
                    firestoreService.createUser(
                        user = User(
                            name = username,
                            email = email,
                            pictureUrl = null,
                            vehicles = null
                        )
                    ) { throwable ->
                        if (throwable == null) {
                            val currUser = accountService.getCurrentUser()?.uid
                            (if (hasCameraImage) cameraResultUri.value else galleryResultUri.value)?.let { uri ->
                                if (currUser != null) {
                                    storageService.uploadPicture(
                                        file = uri,
                                        currentUserId = currUser,
                                        path = PROFILE_FOLDER,
                                        onFailure = { err ->
                                            err?.let { it -> onError(it) }
                                        },
                                        onSuccess = {
                                            firestoreService.updateUser(userId = currUser, value = hashMapOf("pictureUrl" to it)) { error ->
                                                _isButtonLoading.value = false
                                                if (error == null) {
                                                    onNavigation.invoke()
                                                } else {
                                                    onError(error)
                                                }
                                            }
                                        })
                                }
                            }
                            if (cameraResultUri.value == null && galleryResultUri.value == null)
                                onNavigation.invoke()
                        } else {
                            onError(throwable)
                        }
                    }
                } else {
                    _isButtonLoading.value = false
                    onError(error)
                }
            }
        }
    }
}