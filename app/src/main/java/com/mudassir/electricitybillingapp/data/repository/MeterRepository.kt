package com.mudassir.electricitybillingapp.data.repository

import com.mudassir.electricitybillingapp.data.MeterReading
import com.mudassir.electricitybillingapp.data.MeterReadingDao

class MeterRepository(
    private val dao: MeterReadingDao
) {

    suspend fun insertReading(reading: MeterReading) =
        dao.insert(reading)

    suspend fun getLastThree(serviceNumber: String) =
        dao.getLastThree(serviceNumber)

    suspend fun getLastReading(serviceNumber: String) =
        dao.getLastReading(serviceNumber)
}