package com.dvainsolutions.drivie.presentation.signup.vehicle_signup

data class VehicleDataValidationState(
    val licenceError: String? = null,
    val brandError: String? = null,
    val modelError: String? = null,
    val mileageError: String? = null,
    val fuelTypeError: String? = null,
)
