package com.mudassir.electricitybillingapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudassir.electricitybillingapp.data.AppDatabase
import com.mudassir.electricitybillingapp.data.MeterReading
import com.mudassir.electricitybillingapp.data.repository.MeterRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MeterRepository

    init {
        val dao = AppDatabase.getDatabase(application).meterReadingDao()
        repository = MeterRepository(dao)
    }

    suspend fun insertReading(reading: MeterReading) =
        repository.insertReading(reading)

    suspend fun getLastThree(serviceNumber: String) =
        repository.getLastThree(serviceNumber)

    suspend fun getLastReading(serviceNumber: String) =
        repository.getLastReading(serviceNumber)
}