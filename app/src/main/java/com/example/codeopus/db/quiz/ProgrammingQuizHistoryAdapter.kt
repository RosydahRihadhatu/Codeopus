package com.example.codeopus.db.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.R

class ProgrammingQuizHistoryAdapter(private val quizHistoryItems: List<ProgrammingQuizHistoryItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_QUIZ_HISTORY = 1
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_QUIZ_HISTORY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_programming_quiz_history, parent, false)
        return QuizHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as QuizHistoryViewHolder).bind(quizHistoryItems[position] as ProgrammingQuizHistoryItem.QuizHistory)
    }

    override fun getItemCount(): Int = quizHistoryItems.size

    inner class QuizHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextViewDay: TextView = itemView.findViewById(R.id.dateTextViewDay)
        private val dateTextViewMonth: TextView = itemView.findViewById(R.id.dateTextViewMonth)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val category: TextView = itemView.findViewById(R.id.category)
        private val score: TextView = itemView.findViewById(R.id.score)

        fun bind(quizHistoryItem: ProgrammingQuizHistoryItem.QuizHistory) {
            val quizHistory = quizHistoryItem.history
            dateTextViewDay.text = quizHistoryItem.day
            dateTextViewMonth.text = quizHistoryItem.month
            title.text = quizHistory.title
            category.text = quizHistory.category
            score.text = "${quizHistory.score}/${quizHistory.total}"

            if (quizHistory.score < quizHistory.total / 2) {
                score.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            } else {
                score.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            }
        }
    }
}
