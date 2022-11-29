package com.dvainsolutions.drivie.data.model

import android.content.Context
import com.dvainsolutions.drivie.R
import com.google.firebase.Timestamp


data class MiscData(
    val insurance: MiscInsurance? = null,
    val vignette: MiscVignette? = null,
    val weightTax: MiscWeightTax? = null
)

data class MiscInsurance(
    val type: String = InsuranceType.KGFB.name,
    val price: Int? = 0,
    val date: Timestamp = Timestamp.now()
)

enum class InsuranceType(private val typeId: Int) {
    CASCO(R.string.misc_insurance_casco),
    KGFB(R.string.misc_insurance_kgfb);

    fun getLabel(context: Context) =
        context.getString(typeId)
}

data class MiscVignette(
    val vehicleType: String = VignetteVehicleType.B2.name,
    val regionalType: String = "",
    val price: Int? = 0,
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now()
)

enum class VignetteVehicleType {
    D1M,
    D1,
    D2,
    B2,
    U
}

enum class VignetteRegionalType(private val typeId: Int) {
    COUNTRY_WEEKLY(R.string.misc_vignette_regional_type_country_weekly),
    COUNTRY_MONTHLY(R.string.misc_vignette_regional_type_country_monthly),
    COUNTRY_YEARLY(R.string.misc_vignette_regional_type_country_yearly),
    COUNT_YEARLY(R.string.misc_vignette_regional_type_county_yearly);

    fun getLabel(context: Context) =
        context.getString(typeId)
}

data class MiscWeightTax(
    val date: Timestamp = Timestamp.now(),
    val price: Int? = 0
)

enum class MiscTypeList(private val typeId: Int) {
    INSURANCE(R.string.misc_type_insurance),
    VIGNETTE(R.string.misc_type_vignette),
    WEIGHT_TAX(R.string.misc_type_weight_tax);

    fun getLabel(context: Context) =
        context.getString(typeId)
}