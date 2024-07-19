package com.example.codeopus.db

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.R
import com.example.codeopus.db.quiz.QuizHistory

class MyAdapter(
    private val headers: List<HeaderItem>,
    private val language: String,
    private val quizHistory: List<QuizHistory>,
    private val onChildItemClick: (ChildItem) -> Unit
) : RecyclerView.Adapter<MyAdapter.HeaderViewHolder>() {

    private val expandedSections = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val headerItem = headers[position]
        holder.bind(headerItem, position)
    }

    override fun getItemCount(): Int {
        return headers.size
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvHeader)
        private val expandImageView: ImageView = itemView.findViewById(R.id.ivExpand)
        private val childRecyclerView: RecyclerView = itemView.findViewById(R.id.childRecyclerView)

        fun bind(headerItem: HeaderItem, position: Int) {
            titleTextView.text = headerItem.title
            expandImageView.setImageResource(
                if (expandedSections.contains(position)) R.drawable.arah_bawah else R.drawable.arah_kanan
            )

            itemView.setOnClickListener {
                if (expandedSections.contains(position)) {
                    expandedSections.remove(position)
                    childRecyclerView.visibility = View.GONE
                    expandImageView.setImageResource(R.drawable.arah_kanan)
                } else {
                    expandedSections.add(position)
                    childRecyclerView.visibility = View.VISIBLE
                    expandImageView.setImageResource(R.drawable.arah_bawah)
                }
                notifyItemChanged(position)
            }

            childRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            val childAdapter = ChildAdapter(headerItem.childItems, itemView.context, language, headerItem.title, quizHistory, onChildItemClick)
            childRecyclerView.adapter = childAdapter
            childRecyclerView.visibility = if (expandedSections.contains(position)) View.VISIBLE else View.GONE
        }
    }

    inner class ChildAdapter(
        private val childItems: List<ChildItem>,
        private val context: Context,
        private val language: String,
        private val headerTitle: String,
        private val quizHistory: List<QuizHistory>,
        private val onChildItemClick: (ChildItem) -> Unit
    ) : RecyclerView.Adapter<ChildAdapter.ChildViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.child_item, parent, false)
            return ChildViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
            val childItem = childItems[position]
            holder.bind(childItem)
        }

        override fun getItemCount(): Int = childItems.size

        inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val childTitleTextView: TextView = itemView.findViewById(R.id.tvChildItem)
            private val playButton: ImageView = itemView.findViewById(R.id.btn_play)
            private val scoreTextView: TextView = itemView.findViewById(R.id.tvScore)

            fun bind(childItem: ChildItem) {
                childTitleTextView.text = childItem.title

                val quiz = quizHistory.find { it.title == childItem.title && it.level == childItem.level }

                if (quiz != null) {
                    playButton.visibility = View.GONE
                    scoreTextView.visibility = View.VISIBLE
                    scoreTextView.text = "${quiz.score}/${quiz.total}"
                    scoreTextView.setTextColor(
                        if (quiz.score >= 10) Color.GREEN
                        else Color.RED
                    )
                } else {
                    playButton.visibility = View.VISIBLE
                    scoreTextView.visibility = View.GONE
                }

                itemView.setOnClickListener {
                    onChildItemClick(childItem)
                }
            }
        }
    }
}
