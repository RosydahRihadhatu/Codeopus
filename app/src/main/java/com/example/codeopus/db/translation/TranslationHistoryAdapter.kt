package com.example.codeopus.db.translation

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.R
import java.text.SimpleDateFormat
import java.util.*

class TranslationHistoryAdapter(private var historyList: List<TranslationHistory>) :
    RecyclerView.Adapter<TranslationHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagePreview: ImageView = view.findViewById(R.id.image_preview)
        val textCodeopus: TextView = view.findViewById(R.id.text_codeopus)
        val textTimestamp: TextView = view.findViewById(R.id.text_timestamp)
        val textDate: TextView = view.findViewById(R.id.text_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_translation_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]

        // Set preview image if available, otherwise use default
        holder.imagePreview.setImageResource(R.drawable.script) // Use actual image resource or URL here
        holder.textCodeopus.text="codeopus"

        // Format timestamp to yyyyMMdd and generate sequence number
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val dateComponent = dateFormat.format(Date(history.timestamp))

        // Extract hour and minute from the timestamp
        val timeFormat = SimpleDateFormat("HHmm", Locale.getDefault())
        val timeComponent = timeFormat.format(Date(history.timestamp))

        holder.textTimestamp.text = "$dateComponent-$timeComponent"

        // Format date to dd-MM-yyyy
        val dateFormatDisplay = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        holder.textDate.text = dateFormatDisplay.format(Date(history.timestamp))
    }

    override fun getItemCount() = historyList.size

    fun updateData(newHistoryList: List<TranslationHistory>) {
        Log.d("TranslationHistoryAdapter", "Updating data with ${newHistoryList.size} items")
        historyList = newHistoryList
        notifyDataSetChanged()
    }
}
