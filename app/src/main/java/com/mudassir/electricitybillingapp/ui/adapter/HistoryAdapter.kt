package com.mudassir.electricitybillingapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mudassir.electricitybillingapp.R
import com.mudassir.electricitybillingapp.data.MeterReading
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var list: List<MeterReading> = emptyList()

    fun submitList(data: List<MeterReading>) {
        list = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvReading: TextView =
            view.findViewById(R.id.tvReading)

        val tvCost: TextView =
            view.findViewById(R.id.tvCost)

        val tvDate: TextView =
            view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = list[position]

        holder.tvReading.text =
            "Reading: ${item.readingValue}"

        holder.tvCost.text =
            "Cost: $${String.format("%.2f", item.cost)}"

        val format = SimpleDateFormat(
            "dd MMM yyyy HH:mm",
            Locale.getDefault()
        )

        holder.tvDate.text =
            format.format(Date(item.timestamp))
    }
}