package com.dvainsolutions.drivie.data.model

import com.google.firebase.Timestamp

object VehiclePartList {
    val parts = listOf(
        VehiclePart(name = "Gumi (nyári)", maxLifeSpan = 85000, currentHealth = 0, replacementTime = Timestamp.now()),
        VehiclePart(name = "Gumi (téli)", maxLifeSpan = 85000, currentHealth = 0, replacementTime = Timestamp.now()),
        VehiclePart(name = "Féktárcsa (első pár)", maxLifeSpan = 50000, currentHealth = 0, replacementTime = Timestamp.now()),
        VehiclePart(name = "Féktárcsa (hátsó pár)", maxLifeSpan = 50000, currentHealth = 0, replacementTime = Timestamp.now()),
        VehiclePart(name = "Vezérlés", maxLifeSpan = 120000, currentHealth = 0, replacementTime = Timestamp.now()),
        VehiclePart(name = "Felfüggesztés (első pár)", maxLifeSpan = 80000, currentHealth = 0, replacementTime = Timestamp.now()),
        VehiclePart(name = "Felfüggesztés (hátsó pár)", maxLifeSpan = 80000, currentHealth = 0, replacementTime = Timestamp.now()),
        VehiclePart(name = "Olajcsere", maxLifeSpan = 10000, currentHealth = 0, replacementTime = Timestamp.now()),
        VehiclePart(name = "Szűrők cseréje", maxLifeSpan = 10000, currentHealth = 0, replacementTime = Timestamp.now()),
    )
}