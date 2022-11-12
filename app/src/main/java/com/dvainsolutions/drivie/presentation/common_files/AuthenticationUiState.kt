package com.dvainsolutions.drivie.presentation.common_files

data class AuthenticationUiState(
    val userName: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = ""
)
