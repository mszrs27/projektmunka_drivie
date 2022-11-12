package com.dvainsolutions.drivie.presentation.signup.vehicle_signup

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.data.model.convertInputToFuelType
import com.dvainsolutions.drivie.presentation.common_files.InputValidationResult
import com.dvainsolutions.drivie.utils.UiText

class VehicleDataValidation(
    private val licence: String,
    private val brand: String,
    private val model: String,
    private val mileage: String,
    private val fuelType: String,
    private val context: Context,
) {
    fun validateLicence(): InputValidationResult {
        return if (licence.isBlank())
            InputValidationResult(successful = false,
                errorMessage = UiText.StringResource(resId = R.string.error_blank_field)
                    .asString(context)
            )
        else
            InputValidationResult(successful = true)
    }

    fun validateBrand(): InputValidationResult {
        return if (brand.isBlank())
            InputValidationResult(successful = false,
                errorMessage = UiText.StringResource(resId = R.string.error_blank_field)
                    .asString(context)
            )
        else
            InputValidationResult(successful = true)
    }

    fun validateModel(): InputValidationResult {
        return if (model.isBlank())
            InputValidationResult(successful = false,
                errorMessage = UiText.StringResource(resId = R.string.error_blank_field)
                    .asString(context)
            )
        else
            InputValidationResult(successful = true)
    }

    fun validateMileage(): InputValidationResult {
        return if (!mileage.isDigitsOnly())
            InputValidationResult(successful = false,
                errorMessage = UiText.StringResource(resId = R.string.error_only_digits)
                    .asString(context)
            )
        else if (mileage.isBlank())
            InputValidationResult(successful = false,
                errorMessage = UiText.StringResource(resId = R.string.error_blank_field)
                    .asString(context)
            )
        else
            InputValidationResult(successful = true)

    }

    fun validateFuelType(): InputValidationResult {
       return if (fuelType.isNotBlank() && convertInputToFuelType(fuelType) == null)
           InputValidationResult(successful = false,
               errorMessage = UiText.StringResource(resId = R.string.error_fuel_type)
                   .asString(context)
           )
        else
           InputValidationResult(successful = true)
    }
}