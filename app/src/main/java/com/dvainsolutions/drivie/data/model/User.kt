package com.dvainsolutions.drivie.data.model

data class User(
    val name: String = "",
    val email: String = "",
    val pictureUrl: String? = null,
    val vehicles: List<Vehicle>? = null
)
