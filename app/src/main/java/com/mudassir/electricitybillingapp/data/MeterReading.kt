package com.mudassir.electricitybillingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meter_readings")
data class MeterReading(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val serviceNumber: String,
    val readingValue: Int,
    val cost: Double,
    val timestamp: Long = System.currentTimeMillis()
)