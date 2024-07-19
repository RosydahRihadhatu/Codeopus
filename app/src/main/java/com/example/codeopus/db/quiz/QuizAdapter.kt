package com.example.codeopus.db.quiz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.R

data class Quiz(
    val title: String,
    val topicCount: Int,
    val progress: Int,
    val total: Int
)

class QuizAdapter(private var quizList: List<Quiz>, private val listener: OnItemClickListener) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(quiz: Quiz)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizList[position]
        holder.tvQuizTitle.text = quiz.title
        holder.tvTopicCount.text = "${quiz.topicCount} Topik"
        holder.tvProgress.text = "${quiz.progress}/${quiz.total}"
        holder.progressBar.max = quiz.total
        holder.progressBar.progress = quiz.progress

        holder.itemView.setOnClickListener {
            listener.onItemClick(quiz)
        }
    }

    override fun getItemCount() = quizList.size

    fun updateData(newQuizList: List<Quiz>) {
        quizList = newQuizList
        notifyDataSetChanged()
    }

    class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuizTitle: TextView = itemView.findViewById(R.id.tvQuizTitle)
        val tvTopicCount: TextView = itemView.findViewById(R.id.tvTopicCount)
        val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}
