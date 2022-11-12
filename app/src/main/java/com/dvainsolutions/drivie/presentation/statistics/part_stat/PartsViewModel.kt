package com.dvainsolutions.drivie.presentation.statistics.part_stat

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvainsolutions.drivie.R
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager
import com.dvainsolutions.drivie.common.snackbar.SnackbarManager.onError
import com.dvainsolutions.drivie.data.model.VehiclePart
import com.dvainsolutions.drivie.service.FirestoreService
import com.dvainsolutions.drivie.ui.theme.DrivieLightBlue
import com.dvainsolutions.drivie.utils.Constants
import com.dvainsolutions.drivie.utils.Constants.WEEKS
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.roundToInt

@HiltViewModel
class PartsViewModel @Inject constructor(
    private val firestoreService: FirestoreService,
    private val dataStore: DataStore<Preferences>,
    private val application: Application
) : ViewModel() {

    var partList: List<VehiclePart> = listOf()
        private set

    val partData: MutableMap<String, Number> = mutableMapOf()
    var selectedPart by mutableStateOf(VehiclePart())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var b0: Double = -0.0
    var b1: Double = -0.0

    init {
        viewModelScope.launch {
            isLoading = true
            val preferences = dataStore.data.first()
            preferences[stringPreferencesKey(Constants.SELECTED_CAR_ID)]?.let {
                calculateLinearRegression(it)

                firestoreService.getParts(
                    carId = it,
                    onResult = { result ->
                        isLoading = false
                        if (result != null) {
                            partList = result
                            partList.forEach { part ->
                                part.currentHealth?.let { remaining ->
                                    partData.put(part.name, (part.maxLifeSpan - remaining))
                                }
                            }
                        }
                    },
                    onError = {
                        isLoading = false
                        SnackbarManager.showMessage(R.string.error_getting_data)
                    })
            }
        }
    }

    fun createChartData(barChart: BarChart, onShowDialog: () -> Unit, onDismissDialog: () -> Unit) {
        var index = 0f
        val labels = mutableListOf<String>()
        val entries = partData.map { partData ->
            index += 1f
            labels.add(partData.key)
            BarEntry(
                index,
                partData.value.toFloat(),
            )
        }

        val dataSetFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return partData.keys.elementAt(barEntry?.x?.toInt()!! - 1)
                    .toString()
            }

            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return ""
            }
        }

        val yAxisFormatter = object : ValueFormatter() {


            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return "${value.toInt()} km"
            }
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = DrivieLightBlue.toArgb()
            valueFormatter = dataSetFormatter
            valueTextSize = 14f
        }

        val barData = BarData(dataSet)

        barChart.apply {
            xAxis.apply {
                valueFormatter = dataSetFormatter
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
            axisLeft.apply {
                valueFormatter = yAxisFormatter
            }
            axisRight.isEnabled = false
            data = barData
            legend.isEnabled = false
            description.isEnabled = false
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    onShowDialog.invoke()
                    selectPart(e)
                }

                override fun onNothingSelected() {
                    onDismissDialog.invoke()
                }

            })
            invalidate()
        }
    }

    fun selectPart(e: Entry?) {
        selectedPart = partList.get(e?.x?.toInt()!! - 1)
    }

    private fun calculateLinearRegression(carId: String) {
        firestoreService.getTrips(
            carId = carId,
            onResult = { trips ->
                //e.g. 32.week - 232 km
                val drivenKmOnEachWeek: MutableMap<Int, Double> = mutableMapOf()
                val closestWeeks: MutableList<Int> = mutableListOf()

                if (!trips.isNullOrEmpty()) {
                    trips.forEach { trip ->
                        // get driven km on each week
                        val date = trip.date?.toDate() ?: Timestamp.now().toDate()
                        val calendar = Calendar.getInstance()
                        calendar.time = date
                        val dayOfWeek = calendar.get(Calendar.WEEK_OF_YEAR)

                        drivenKmOnEachWeek[dayOfWeek] =
                            drivenKmOnEachWeek.getOrDefault(
                                dayOfWeek,
                                0.0
                            ) + trip.distance.toDouble()
                        val currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)

                        // get the closest 4 weeks to the current week
                        repeat(WEEKS) {
                            val closestWeek =
                                drivenKmOnEachWeek.keys.fold(null) { acc: Int?, week ->
                                    if (week <= currentWeek && (acc == null || week > acc) && !closestWeeks.contains(
                                            week
                                        )
                                    ) week
                                    else acc
                                }
                            if (closestWeek != null) {
                                closestWeeks.add(closestWeek)
                            }
                        }
                    }

                    // accumulate the data of the closest 4 weeks by driven km
                    val accumulatedDrivenKmInLastMonthByWeek: MutableMap<Double, Double> =
                        mutableMapOf()
                    repeat(WEEKS) {
                        accumulatedDrivenKmInLastMonthByWeek[it.toDouble()] =
                            accumulatedDrivenKmInLastMonthByWeek.getOrDefault(
                                it.toDouble(),
                                0.0
                            ) + drivenKmOnEachWeek.getOrDefault(
                                closestWeeks[it],
                                0.0
                            ) + accumulatedDrivenKmInLastMonthByWeek.getOrDefault(
                                it.toDouble() - 1,
                                0.0
                            )
                    }

                    calculate(
                        accumulatedDrivenKmInLastMonthByWeek.keys.toList(),
                        accumulatedDrivenKmInLastMonthByWeek.values.toList()
                    )
                }
            },
            onError = {
                if (it != null) {
                    onError(it)
                }
            }
        )
    }

    private fun calculate(independentVariables: List<Double>, dependentVariables: List<Double>) {
        val meanX = independentVariables.sum().div(independentVariables.count())
        val meanY = dependentVariables.sum().div(dependentVariables.count())

        val variance: Double =
            independentVariables.stream().mapToDouble { (it - meanX).pow(2) }.sum()

        var covariance = 0.0
        for (i in independentVariables.indices) {
            val xPart = independentVariables[i] - meanX
            val yPart = dependentVariables[i] - meanY
            covariance += xPart * yPart
        }

        b1 = covariance.div(variance)
        b0 = meanY - b1 * meanX
    }

    fun predict(remainingHealth: Int): String {
        if (b0 == -0.0 && b1 == -0.0) return application.getString(R.string.vehicle_parts_wear_cant_calculate)
        val remainingWeek = ((remainingHealth - b0) / b1).roundToInt()
        val deadline = LocalDate.now(ZoneId.systemDefault()).plusWeeks(remainingWeek.toLong())
        return deadline.toString()
    }
}