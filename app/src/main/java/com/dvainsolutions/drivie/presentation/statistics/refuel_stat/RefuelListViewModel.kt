package com.dvainsolutions.drivie.presentation.statistics.refuel_stat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager.onError
import com.dvainsolutions.drivie.data.model.Refueling
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RefuelListViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    var refuelings = mutableStateListOf<Refueling>()
        private set

    var isLoading by mutableStateOf(false)

    init {
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            preferences[stringPreferencesKey(Constants.SELECTED_CAR_ID)]?.let { carId ->
                isLoading = true
                firestoreService.getRefuels(
                    carId = carId,
                    onResult = { refuels ->
                        isLoading = false
                        refuels?.forEach {
                            refuelings.add(it)
                        }
                    },
                    onError = { err ->
                        isLoading = false
                        err?.let { it ->
                            onError(
                                it
                            )
                        }
                    }
                )
            }
        }
    }
}