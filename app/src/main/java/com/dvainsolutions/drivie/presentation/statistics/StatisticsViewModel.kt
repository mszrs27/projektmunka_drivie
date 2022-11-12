package com.dvainsolutions.drivie.presentation.statistics

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
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    var avgData = mutableStateListOf<String?>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val preferences = dataStore.data.first()
            preferences[stringPreferencesKey(Constants.SELECTED_CAR_ID)]?.let { carId ->
                isLoading = true
                firestoreService.getAvgDataFromTrips(
                    carId = carId,
                    onResult = { result ->
                        isLoading = false
                        result.forEach {
                            avgData.add(it)
                        }
                    },
                    onError = { err ->
                        isLoading = false
                        err?.let { it -> onError(it) }
                    }
                )
            }
        }
    }


}