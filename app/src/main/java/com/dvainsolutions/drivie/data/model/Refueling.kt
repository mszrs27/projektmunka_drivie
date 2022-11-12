package com.dvainsolutions.drivie.data.model

import com.google.firebase.Timestamp

data class Refueling(
    val vehicle: String = "",
    val date: Timestamp? = Timestamp.now(),
    val location: String? = null,
    val type: FuelType? = null,
    val quantity: Float? = null,
    val cost: Int? = null,
    val mileage: Int? = null,
    val id: String = ""
)
