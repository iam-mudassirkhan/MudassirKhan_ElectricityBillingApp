package com.mudassir.electricitybillingapp.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mudassir.electricitybillingapp.R
import com.mudassir.electricitybillingapp.model.Slab
import java.io.InputStreamReader

object SlabLoader {

    fun loadSlabs(context: Context): List<Slab> {

        val inputStream = context.resources.openRawResource(R.raw.slabs)
        val reader = InputStreamReader(inputStream)

        val type = object : TypeToken<List<Slab>>() {}.type

        return Gson().fromJson(reader, type)
    }
}