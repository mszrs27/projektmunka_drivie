package com.dvainsolutions.drivie.presentation.signup.vehicle_signup

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.ext.toDate
import com.dvainsolutions.drivie.common.ext.toDateString
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarMessage
import com.dvainsolutions.drivie.data.model.*
import com.dvainsolutions.drivie.service.AccountService
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.service.StorageService
import com.dvainsolutions.drivie.utils.Constants.VEHICLE_FOLDER
import com.dvainsolutions.drivie.utils.TextRecognition
import com.dvainsolutions.drivie.utils.UiText
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class VehicleSignupViewModel @Inject constructor(
    private val application: Application,
    private val firestoreService: FirestoreService,
    private val accountService: AccountService,
    private val storageService: StorageService
) : ViewModel() {

    var uiState = mutableStateOf(VehicleSignupUiState())
        private set

    var vehicleDataValidationState by mutableStateOf(VehicleDataValidationState())
        private set

    var hasCameraImage by mutableStateOf(false)
    val cameraResultUri = mutableStateOf<Uri?>(null)
    val cameraResultUriForTextRecognition = mutableStateOf<Uri?>(null)
    val galleryResultUri = mutableStateOf<Uri?>(null)

    private var _isButtonLoading = mutableStateOf(false)
    val isButtonLoading: State<Boolean> = _isButtonLoading

    val emissionList = EmissionStandard.values().map { it.standard }.toList()
    private var partName by mutableStateOf(VehiclePartList.parts.first().name)
    private var _partMileage = mutableStateOf("")
    var partMileage: State<String> = _partMileage
    val inputParts = mutableStateMapOf<String, Int>()

    private val carName get() = uiState.value.carName
    private val licence get() = uiState.value.licence
    private val brand get() = uiState.value.brand
    private val model get() = uiState.value.model
    private val engine get() = uiState.value.engine
    private val consumption get() = uiState.value.consumption
    private val mileage get() = uiState.value.mileage
    private val fuelType get() = uiState.value.fuelType
    private val fuelSize get() = uiState.value.fuelSize
    private val motDate get() = uiState.value.motDate
    private val emissionStandard get() = uiState.value.emissionStandard
    private val insurance get() = uiState.value.insurance

    fun onCarNameChange(value: String) {
        uiState.value = uiState.value.copy(carName = value)
    }

    fun onLicenceChange(value: String) {
        vehicleDataValidationState = vehicleDataValidationState.copy(licenceError = null)
        uiState.value = uiState.value.copy(licence = value)
    }

    fun onBrandChange(value: String) {
        vehicleDataValidationState = vehicleDataValidationState.copy(brandError = null)
        uiState.value = uiState.value.copy(brand = value)
    }

    fun onModelChange(value: String) {
        vehicleDataValidationState = vehicleDataValidationState.copy(modelError = null)
        uiState.value = uiState.value.copy(model = value)
    }

    fun onEngineChange(value: String) {
        uiState.value = uiState.value.copy(engine = value)
    }

    fun onMileageChange(value: String) {
        vehicleDataValidationState = vehicleDataValidationState.copy(mileageError = null)
        uiState.value = uiState.value.copy(mileage = value)
    }

    fun onConsumptionChange(value: String) {
        uiState.value = uiState.value.copy(consumption = value)
    }

    fun onFuelTypeChange(value: String) {
        vehicleDataValidationState = vehicleDataValidationState.copy(fuelTypeError = null)
        uiState.value = uiState.value.copy(fuelType = value)
    }

    fun onFuelSizeChange(value: String) {
        uiState.value = uiState.value.copy(fuelSize = value)
    }

    fun onMotDateChange(value: Calendar) {
        uiState.value = uiState.value.copy(motDate = value.toDateString())
    }

    fun onEmissionStandardChange(value: String) {
        uiState.value = uiState.value.copy(emissionStandard = value)
    }

    fun onInsuranceChange(value: Calendar) {
        uiState.value = uiState.value.copy(insurance = value.toDateString())
    }

    fun onSaveVehicleData(onNavigation: () -> Unit) {
        val validation = VehicleDataValidation(
            licence = licence,
            brand = brand,
            model = model,
            mileage = mileage,
            fuelType = fuelType,
            context = application.baseContext
        )
        val licenceResult = validation.validateLicence()
        val brandResult = validation.validateBrand()
        val modelResult = validation.validateModel()
        val mileageResult = validation.validateMileage()
        val fuelTypeResult = validation.validateFuelType()

        val hasError = listOf(
            licenceResult,
            brandResult,
            modelResult,
            mileageResult,
            fuelTypeResult
        ).any { !it.successful }

        if (hasError) {
            vehicleDataValidationState = vehicleDataValidationState.copy(
                modelError = modelResult.errorMessage,
                brandError = brandResult.errorMessage,
                licenceError = licenceResult.errorMessage,
                mileageError = mileageResult.errorMessage,
                fuelTypeError = fuelTypeResult.errorMessage,
            )
        } else {
            viewModelScope.launch {
                _isButtonLoading.value = true
                val currUserId = accountService.getCurrentUser()?.uid
                val vehicle = Vehicle(
                    nickname = carName,
                    licence = licence,
                    brand = brand,
                    model = model,
                    engineCapacity = engine.toIntOrNull(),
                    consumption = consumption.toDoubleOrNull(),
                    mileage = mileage.toInt(),
                    fuelType = convertInputToFuelType(fuelType),
                    fuelSize = fuelSize.toIntOrNull(),
                    emissionType = EmissionStandard.values()
                        .first { it.standard == emissionStandard },
                    motDate = motDate.toDate()?.let { Timestamp(it) },
                    insuranceDate = insurance.toDate()?.let { Timestamp(it) },
                    pictureUrl = null,
                    parts = calculatePartsMileage()
                )
                if (currUserId != null) {
                    firestoreService.createVehicle(userId = currUserId, value = vehicle) { task ->
                        if (task.exception == null) {
                            firestoreService.updateVehicle(userId = currUserId, vehicleId = task.result.id, value = hashMapOf("id" to task.result.id), onResult = {
                                (if (hasCameraImage) cameraResultUri.value else galleryResultUri.value)?.let { uri ->
                                    storageService.uploadPicture(
                                        file = uri,
                                        currentUserId = currUserId,
                                        path = VEHICLE_FOLDER,
                                        onFailure = { err ->
                                            err?.let { it -> SnackbarManager.onError(it) }
                                        },
                                        onSuccess = {
                                            firestoreService.updateVehicle(
                                                userId = currUserId,
                                                vehicleId = task.result.id,
                                                value = hashMapOf("pictureUrl" to it)
                                            ) { error ->
                                                _isButtonLoading.value = false
                                                if (error == null) {
                                                    onNavigation.invoke()
                                                } else {
                                                    SnackbarManager.onError(error)
                                                }
                                            }
                                        })
                                }
                            })
                            if (cameraResultUri.value == null && galleryResultUri.value == null)
                                onNavigation.invoke()
                        } else {
                            _isButtonLoading.value = false
                            SnackbarManager.onError(task.exception!!)
                        }
                    }
                }
            }
        }
    }

    private fun calculatePartsMileage(): MutableList<VehiclePart> {
        val partListToUpload: MutableList<VehiclePart> = mutableListOf()
        inputParts.keys.forEachIndexed { index, partName ->
            VehiclePartList.parts.firstOrNull { it.name == partName }?.apply {
                currentHealth = maxLifeSpan - inputParts.values.elementAt(index)
                partListToUpload.add(this)
            }
        }
        return partListToUpload
    }

    fun runTextRecognition(onResult: () -> Unit) {
        cameraResultUriForTextRecognition.value.let { res ->
            if (res != null) {
                TextRecognition(application.baseContext).runTextRecognition(res) { blocks ->
                    blocks.forEach { block ->
                        block.lines.forEach {
                            processLine(it.text)
                        }
                    }
                    onResult.invoke()
                }
            }
        }
    }

    private fun processLine(line: String) {
        if (line.contains("A") && (line.length in 3..13) &&
            (line.uppercase() != "MAGYARORSZAG") && line.uppercase() != "MAGYARORSZÁG"
        ) {
            if (uiState.value.licence.isEmpty()) {
                uiState.value = uiState.value.copy(licence = line.removeRange(0, 2))
            }
        }
        if (line.contains("D.1")) {
            val text = try {
                line
                    .substring(line.indexOf("D.1") + ("D.1".length) + 1)
                    .uppercase()
            } catch (e: Exception) {
                null
            }
            uiState.value = uiState.value.copy(brand = text ?: "")
        }
        if (line.contains("D.3")) {
            val text = try {
                line
                    .substring(line.indexOf("D.3") + ("D.3".length) + 1)
                    .uppercase()
            } catch (e: Exception) {
                null
            }
            uiState.value = uiState.value.copy(model = text ?: "")
        }
        if (line.contains("P.1")) {
            val text = try {
                line
                    .substring(line.indexOf("P.1") + ("P.1".length) + 1)
            } catch (e: Exception) {
                null
            }

            var engineSubStr: String? = ""
            if (text != null) {
                engineSubStr =
                    try {
                        text.substring(0, text.indexOf("CM3") - 1)
                    } catch (e: Exception) {
                        null
                    }
            }
            uiState.value = uiState.value.copy(engine = engineSubStr ?: text ?: "")
        }
        if (line.contains("P.3")) {
            val text = try {
                line
                    .substring(line.indexOf("P.3") + ("P.3".length) + 1)
                    .lowercase()
                    .replaceFirstChar { char -> char.uppercase() }
            } catch (e: Exception) {
                null
            }
            uiState.value = uiState.value.copy(fuelType = text ?: "")
        }
    }

    fun setPartNameText(text: String) {
        partName = text
    }

    fun setPartMileageText(text: String) {
        _partMileage.value = text
    }

    fun addItemsToLists(onResult: () -> Unit) {
        if (!inputParts.containsKey(partName)) {
            if (_partMileage.value.isEmpty()) _partMileage.value = "0"
            inputParts.put(partName, _partMileage.value.toInt())
            _partMileage.value = ""

            onResult.invoke()
        } else if (partName.isEmpty() || partName.isBlank()) {
            SnackbarManager.showMessage(
                SnackbarMessage.StringSnackbar(
                    UiText.StringResource(resId = R.string.error_choose_part)
                        .asString(application)
                )
            )
        } else {
            SnackbarManager.showMessage(
                SnackbarMessage.StringSnackbar(
                    UiText.StringResource(resId = R.string.error_duplicate_part_in_list)
                        .asString(application)
                )
            )
        }
    }

    fun removeItemsFromLists(partName: String) {
        inputParts.remove(partName)
    }
}