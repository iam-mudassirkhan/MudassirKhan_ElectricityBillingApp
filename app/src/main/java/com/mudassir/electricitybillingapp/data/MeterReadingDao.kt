package com.mudassir.electricitybillingapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MeterReadingDao {

    @Insert
    suspend fun insert(reading: MeterReading)

    @Query("""
        SELECT * FROM meter_readings
        WHERE serviceNumber = :serviceNumber
        ORDER BY timestamp DESC
        LIMIT 3
    """)
    suspend fun getLastThree(serviceNumber: String): List<MeterReading>

    @Query("""
        SELECT * FROM meter_readings
        WHERE serviceNumber = :serviceNumber
        ORDER BY timestamp DESC
        LIMIT 1
    """)
    suspend fun getLastReading(serviceNumber: String): MeterReading?
}