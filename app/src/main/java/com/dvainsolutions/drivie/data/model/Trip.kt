package com.dvainsolutions.drivie.data.model

import com.google.firebase.Timestamp

data class Trip(
    var vehicle: String = "",
    var date: Timestamp? = Timestamp.now(),
    var startLocation: String = "",
    var targetLocation: String = "",
    var distance: Float = 0f,
    var mileage: String = "",
    var consumption: Double = 0.0,
    var id: String = ""
)
