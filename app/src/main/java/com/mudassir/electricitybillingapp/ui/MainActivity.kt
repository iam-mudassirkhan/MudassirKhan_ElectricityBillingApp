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
import com.mudassir.electricitybillingapp.databinding.ActivityMainBinding
import com.mudassir.electricitybillingapp.model.Slab
import com.mudassir.electricitybillingapp.ui.adapter.HistoryAdapter
import com.mudassir.electricitybillingapp.ui.viewmodel.MainViewModel
import com.mudassir.electricitybillingapp.utils.CostCalculator
import com.mudassir.electricitybillingapp.utils.SlabLoader
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var slabs: List<Slab>

    private lateinit var historyAdapter: HistoryAdapter

    private var calculatedCost: Double = 0.0
    private var currentReadingValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        slabs = SlabLoader.loadSlabs(this)

        binding.btnSubmit.setOnClickListener {
            validateAndProcess()
        }

        binding.btnSave.setOnClickListener {
            saveReading()
        }
    }

    private fun setupRecyclerView() {

        historyAdapter = HistoryAdapter()

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }
    }

    private fun validateAndProcess() {

        clearErrors()

        val serviceNumber =
            binding.etServiceNumber.text.toString().trim()

        val readingText =
            binding.etReading.text.toString().trim()

        // Validate Service Number
        if (!serviceNumber.matches(Regex("^[a-zA-Z0-9]{10}$"))) {

            binding.tilServiceNumber.error =
                "Service number must be 10 alphanumeric characters"

            return
        }

        // Validate Reading
        if (readingText.isEmpty()) {

            binding.tilReading.error =
                "Please enter meter reading"

            return
        }

        val readingValue = readingText.toInt()

        if (readingValue < 0) {

            binding.tilReading.error =
                "Reading cannot be negative"

            return
        }

        lifecycleScope.launch {

            val lastReading =
                viewModel.getLastReading(serviceNumber)

            val consumption: Int

            if (lastReading != null) {

                if (readingValue < lastReading.readingValue) {

                    binding.tilReading.error =
                        "Reading cannot be less than previous reading"

                    return@launch
                }

                consumption =
                    readingValue - lastReading.readingValue

                loadHistory(serviceNumber)

            } else {

                consumption = readingValue
                binding.rvHistory.visibility = View.GONE
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

            binding.rvHistory.visibility = View.VISIBLE
            historyAdapter.submitList(lastThree)

        } else {

            binding.rvHistory.visibility = View.GONE
        }
    }

    private fun showCost(cost: Double) {

        binding.tvCost.text =
            "Total Cost: $${String.format("%.2f", cost)}"

        binding.btnSave.visibility = View.VISIBLE
    }

    private fun saveReading() {

        val serviceNumber =
            binding.etServiceNumber.text.toString().trim()

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

        binding.tilServiceNumber.error = null
        binding.tilReading.error = null
    }

    private fun resetScreen() {

        binding.etReading.text?.clear()
        binding.tvCost.text = ""

        binding.btnSave.visibility = View.GONE
        binding.rvHistory.visibility = View.GONE
    }
}