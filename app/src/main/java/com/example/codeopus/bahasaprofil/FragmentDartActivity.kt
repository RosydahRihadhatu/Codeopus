package com.example.codeopus.bahasaprofil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.db.MyApplication
import com.example.codeopus.R
import com.example.codeopus.db.quiz.ProgrammingQuizHistoryAdapter
import com.example.codeopus.db.quiz.ProgrammingQuizHistoryItem
import com.example.codeopus.db.quiz.QuizHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class FragmentDartActivity : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var adapter: ProgrammingQuizHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_fragment_dart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyTextView = view.findViewById(R.id.emptyTextView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadPassedQuizzes("Dart")
    }

    private fun loadPassedQuizzes(language: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val passedQuizzesList = MyApplication.getDatabase().quizHistoryDao().getAllQuizHistory()
            val passedQuizHistory = passedQuizzesList.filter {
                it.language == language && it.score >= 10
            }

            val groupedQuizHistory = groupQuizHistoryByDate(passedQuizHistory)

            withContext(Dispatchers.Main) {
                if (groupedQuizHistory.isEmpty()) {
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter = ProgrammingQuizHistoryAdapter(groupedQuizHistory)
                    recyclerView.adapter = adapter
                }
            }
        }
    }

    private fun groupQuizHistoryByDate(quizHistoryList: List<QuizHistory>): List<ProgrammingQuizHistoryItem> {
        val groupedItems = mutableListOf<ProgrammingQuizHistoryItem>()
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        val groupedByDate = quizHistoryList.groupBy {
            val date: Date = inputDateFormat.parse(it.date)
            outputDateFormat.format(date)
        }

        for ((date, histories) in groupedByDate) {
            val dateParts = date.split(" ")
            val day = dateParts[0]
            val month = dateParts[1]

            for (history in histories) {
                groupedItems.add(ProgrammingQuizHistoryItem.QuizHistory(history, day, month))
            }
        }

        return groupedItems
    }
}
