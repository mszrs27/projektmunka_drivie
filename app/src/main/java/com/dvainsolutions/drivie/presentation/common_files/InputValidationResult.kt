package com.dvainsolutions.drivie.presentation.common_files

data class InputValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)