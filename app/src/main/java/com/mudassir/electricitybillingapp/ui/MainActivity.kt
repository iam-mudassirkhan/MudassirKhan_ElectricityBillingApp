package com.mudassir.electricitybillingapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mudassir.electricitybillingapp.R
import com.mudassir.electricitybillingapp.data.MeterReading
import com.mudassir.electricitybillingapp.model.Slab
import com.mudassir.electricitybillingapp.ui.adapter.HistoryAdapter
import com.mudassir.electricitybillingapp.ui.viewmodel.MainViewModel
import com.mudassir.electricitybillingapp.utils.CostCalculator
import com.mudassir.electricitybillingapp.utils.SlabLoader
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var slabs: List<Slab>

    private lateinit var tilServiceNumber: TextInputLayout
    private lateinit var etServiceNumber: TextInputEditText

    private lateinit var tilReading: TextInputLayout
    private lateinit var etReading: TextInputEditText

    private lateinit var btnSubmit: Button
    private lateinit var btnSave: Button
    private lateinit var tvCost: TextView
    private lateinit var rvHistory: RecyclerView

    private lateinit var historyAdapter: HistoryAdapter

    private var calculatedCost: Double = 0.0
    private var currentReadingValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()

        slabs = SlabLoader.loadSlabs(this)

        btnSubmit.setOnClickListener {
            validateAndProcess()
        }

        btnSave.setOnClickListener {
            saveReading()
        }
    }


    private fun initViews() {

        tilServiceNumber = findViewById(R.id.tilServiceNumber)
        etServiceNumber = findViewById(R.id.etServiceNumber)

        tilReading = findViewById(R.id.tilReading)
        etReading = findViewById(R.id.etReading)

        btnSubmit = findViewById(R.id.btnSubmit)
        btnSave = findViewById(R.id.btnSave)
        tvCost = findViewById(R.id.tvCost)
        rvHistory = findViewById(R.id.rvHistory)
    }


    private fun setupRecyclerView() {

        historyAdapter = HistoryAdapter()

        rvHistory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }
    }


    private fun validateAndProcess() {

        clearErrors()

        val serviceNumber = etServiceNumber.text.toString().trim()
        val readingText = etReading.text.toString().trim()

        // Validate Service Number
        if (!serviceNumber.matches(Regex("^[a-zA-Z0-9]{10}$"))) {
            tilServiceNumber.error =
                "Service number must be 10 alphanumeric characters"
            return
        }

        // validate reading
        if (readingText.isEmpty()) {
            tilReading.error = "Please enter meter reading"
            return
        }

        val readingValue = readingText.toInt()

        if (readingValue < 0) {
            tilReading.error = "Reading cannot be negative"
            return
        }

        lifecycleScope.launch {

            val lastReading =
                viewModel.getLastReading(serviceNumber)

            val consumption: Int

            if (lastReading != null) {

                if (readingValue < lastReading.readingValue) {
                    tilReading.error =
                        "Reading cannot be less than previous reading"
                    return@launch
                }

                consumption =
                    readingValue - lastReading.readingValue

                loadHistory(serviceNumber)

            } else {
                consumption = readingValue
                rvHistory.visibility = View.GONE
            }

            calculatedCost =
                CostCalculator.calculateCost(consumption, slabs)

            currentReadingValue = readingValue

            showCost(calculatedCost)
        }
    }


    private suspend fun loadHistory(serviceNumber: String) {

        val lastThree =
            viewModel.getLastThree(serviceNumber)

        if (lastThree.isNotEmpty()) {
            rvHistory.visibility = View.VISIBLE
            historyAdapter.submitList(lastThree)
        } else {
            rvHistory.visibility = View.GONE
        }
    }

    private fun showCost(cost: Double) {

        tvCost.text =
            "Total Cost: $${String.format("%.2f", cost)}"

        btnSave.visibility = View.VISIBLE
    }

    private fun saveReading() {

        val serviceNumber =
            etServiceNumber.text.toString().trim()

        lifecycleScope.launch {

            val meterReading = MeterReading(
                serviceNumber = serviceNumber,
                readingValue = currentReadingValue,
                cost = calculatedCost
            )

            viewModel.insertReading(meterReading)

            Toast.makeText(
                this@MainActivity,
                "Reading saved successfully",
                Toast.LENGTH_SHORT
            ).show()

            resetScreen()
        }
    }

    private fun clearErrors() {
        tilServiceNumber.error = null
        tilReading.error = null
    }

    private fun resetScreen() {
        etReading.text?.clear()
        tvCost.text = ""
        btnSave.visibility = View.GONE
        rvHistory.visibility = View.GONE
    }
}