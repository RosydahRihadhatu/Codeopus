package com.example.codeopus.db.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.R

class QuizHistoryAdapter(private val quizHistoryItems: List<QuizHistoryItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DATE_HEADER = 0
        private const val VIEW_TYPE_QUIZ_HISTORY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (quizHistoryItems[position]) {
            is QuizHistoryItem.DateHeader -> VIEW_TYPE_DATE_HEADER
            is QuizHistoryItem.QuizHistory -> VIEW_TYPE_QUIZ_HISTORY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            VIEW_TYPE_QUIZ_HISTORY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz_history, parent, false)
                QuizHistoryViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateHeaderViewHolder -> holder.bind(quizHistoryItems[position] as QuizHistoryItem.DateHeader)
            is QuizHistoryViewHolder -> holder.bind(quizHistoryItems[position] as QuizHistoryItem.QuizHistory)
        }
    }

    override fun getItemCount(): Int = quizHistoryItems.size

    inner class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        fun bind(dateHeader: QuizHistoryItem.DateHeader) {
            dateTextView.text = dateHeader.date
        }
    }

    inner class QuizHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val category: TextView = itemView.findViewById(R.id.category)
        private val score: TextView = itemView.findViewById(R.id.score)

        fun bind(quizHistoryItem: QuizHistoryItem.QuizHistory) {
            val quizHistory = quizHistoryItem.history
            title.text = quizHistory.title
            category.text = quizHistory.category
            score.text = "${quizHistory.score}/${quizHistory.total}"

            // Set icon based on category or title
            if (quizHistory.category.contains("Python")) {
                icon.setImageResource(R.drawable.python)
            } else if (quizHistory.category.contains("Kotlin")) {
                icon.setImageResource(R.drawable.kotlin)
            } else if (quizHistory.category.contains("Dart")) {
                icon.setImageResource(R.drawable.dart)
            } else if (quizHistory.category.contains("Java")) {
                icon.setImageResource(R.drawable.java)
            } else if (quizHistory.category.contains("HTML")) {
                icon.setImageResource(R.drawable.html)
            } else if (quizHistory.category.contains("CSS")) {
                icon.setImageResource(R.drawable.css)
            } else if (quizHistory.category.contains("JavaScript")) {
                icon.setImageResource(R.drawable.js)
            } else {
                icon.setImageResource(R.drawable.ic_launcher_background)
            }

            // Change the score color based on the result
            if (quizHistory.score <10) {
                score.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            } else {
                score.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            }
        }
    }
}
