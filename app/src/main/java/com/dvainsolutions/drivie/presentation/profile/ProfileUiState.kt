package com.dvainsolutions.drivie.presentation.profile

data class ProfileUiState(
    val pictureUrl: String? = "",
    val isLoading: Boolean = false,
    val name: String? = "",
    val email: String? = "",
    val oldPassword: String = "",
    val password: String = "",
    val passwordAgain: String = ""
)