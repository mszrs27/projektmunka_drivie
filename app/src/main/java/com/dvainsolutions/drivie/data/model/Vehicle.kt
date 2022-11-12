package com.dvainsolutions.drivie.data.model

import com.google.firebase.Timestamp

data class Vehicle(
    val nickname: String? = null,
    val licence: String = "",
    val brand: String = "",
    val model: String = "",
    val engineCapacity: Int? = null,
    val consumption: Double? = null,
    val mileage: Int? = null,
    val fuelType: FuelType? = null,
    val fuelSize: Int? = null,
    val emissionType: EmissionStandard? = null,
    val motDate: Timestamp? = null,
    val insuranceDate: Timestamp? = null,
    val pictureUrl: String? = null,
    val parts: List<VehiclePart>? = null,
    val id: String = ""
)
