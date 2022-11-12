package com.dvainsolutions.drivie.data.model

import android.content.Context
import com.dvainsolutions.drivie.R

enum class FuelType(private val typeId: Int) {
    DIESEL(R.string.fuel_type_diesel),
    PETROL(R.string.fuel_type_petrol),
    ELECTRIC(R.string.fuel_type_electric),
    LPG(R.string.fuel_type_lpg),
    HYBRID(R.string.fuel_type_hybrid),
    HYB_E_G(R.string.fuel_type_hybeg),
    HYB_E_B(R.string.fuel_type_hybeb);

    fun getLabel(context: Context) =
        context.getString(typeId)
}

class FuelTypeList(context: Context) {
    val fuel = listOf(
        FuelType.DIESEL.getLabel(context),
        FuelType.PETROL.getLabel(context),
        FuelType.ELECTRIC.getLabel(context),
        FuelType.LPG.getLabel(context),
        FuelType.HYBRID.getLabel(context),
        FuelType.HYB_E_G.getLabel(context),
        FuelType.HYB_E_B.getLabel(context)
    )
}

fun convertInputToFuelType(value: String?): FuelType? {
    return if (value?.uppercase() == "GÁZOLAJ" || value?.uppercase() == "DÍZEL" || value?.uppercase() == "DIESEL") {
        FuelType.DIESEL
    } else if (value?.uppercase() == "BENZIN") {
        FuelType.PETROL
    } else if (value?.uppercase() == "ELEKTROMOS") {
        FuelType.ELECTRIC
    } else if (value?.uppercase() == "LPG") {
        FuelType.LPG
    } else if (value?.uppercase() == "HIBRID") {
        FuelType.HYBRID
    } else if (value?.uppercase() == "HYB/E/G") {
        FuelType.HYB_E_G
    } else if (value?.uppercase() == "HYB/E/B") {
        FuelType.HYB_E_B
    } else {
        null
    }
}
