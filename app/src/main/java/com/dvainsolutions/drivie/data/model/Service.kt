package com.dvainsolutions.drivie.data.model

import com.google.firebase.Timestamp

data class Service(
    var vehicle: String = "",
    var date: Timestamp? = Timestamp.now(),
    var replacedParts: Map<String, Int> = mapOf(),
    var jobsDone: Map<String, Int> = mapOf(),
    var mileage: Int? = 0,
    var id: String = ""
)
