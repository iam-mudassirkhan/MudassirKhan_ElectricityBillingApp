package com.mudassir.electricitybillingapp.utils

import com.mudassir.electricitybillingapp.model.Slab
import kotlin.math.min

object CostCalculator {

    fun calculateCost(units: Int, slabs: List<Slab>): Double {

        var remainingUnits = units
        var totalCost = 0.0

        for (slab in slabs) {

            if (remainingUnits <= 0) break

            val slabCapacity = slab.maxUnit?.let {
                it - slab.minUnit + 1
            } ?: remainingUnits

            val unitsInSlab = min(remainingUnits, slabCapacity)

            totalCost += unitsInSlab * slab.rate
            remainingUnits -= unitsInSlab
        }

        return totalCost
    }
}