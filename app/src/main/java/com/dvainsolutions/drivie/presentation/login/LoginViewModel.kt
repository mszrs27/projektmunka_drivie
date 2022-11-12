package com.dvainsolutions.drivie.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvainsolutions.drivie.common.ext.isValidEmail
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager.onError
import com.dvainsolutions.drivie.presentation.common_files.AuthenticationUiState
import com.dvainsolutions.drivie.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dvainsolutions.drivie.R.string as AppStringRes

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
) : ViewModel() {

    var uiState = mutableStateOf(AuthenticationUiState())
        private set

    private var _isButtonLoading = mutableStateOf(false)
    val isButtonLoading: State<Boolean> = _isButtonLoading

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(onNavigation: () -> Unit) {
        if (_isButtonLoading.value) return
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppStringRes.error_email)
            return
        }

        if (password.isBlank()) {
            SnackbarManager.showMessage(AppStringRes.error_empty_pass)
            return
        }

        viewModelScope.launch {
            _isButtonLoading.value = true
            accountService.authenticate(email, password) { error ->
                _isButtonLoading.value = false
                if (error == null) {
                    onNavigation.invoke()
                } else {
                    onError(error)
                }
            }
        }
    }
}