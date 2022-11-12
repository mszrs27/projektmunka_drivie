package com.dvainsolutions.drivie.data.model

import com.google.firebase.Timestamp

data class VehiclePart(
    val name: String = "",
    val maxLifeSpan: Int = 0,
    var currentHealth: Int? = null,
    var replacementTime: Timestamp? = null
)