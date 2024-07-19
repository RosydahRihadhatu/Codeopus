package com.example.codeopus.riwayat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.db.MyApplication
import com.example.codeopus.R
import com.example.codeopus.db.quiz.QuizHistory
import com.example.codeopus.db.quiz.QuizHistoryItem
import com.example.codeopus.db.quiz.QuizHistoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentRiwayatKuisActivity : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QuizHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_riwayat_kuis_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.quizHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadQuizHistory()
    }

    private fun loadQuizHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val quizHistoryList = MyApplication.getDatabase().quizHistoryDao().getAllQuizHistory()
            val groupedQuizHistory = groupQuizHistoryByDate(quizHistoryList)
            withContext(Dispatchers.Main) {
                adapter = QuizHistoryAdapter(groupedQuizHistory)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun groupQuizHistoryByDate(quizHistoryList: List<QuizHistory>): List<QuizHistoryItem> {
        val groupedItems = mutableListOf<QuizHistoryItem>()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val currentDate = Calendar.getInstance()
        var currentDateString: String? = null

        quizHistoryList.sortedByDescending { it.getDateInMillis() }.forEach { history ->
            val historyDate = Calendar.getInstance().apply { timeInMillis = history.getDateInMillis() }
            val date = when {
                isSameDay(currentDate, historyDate) -> "Terbaru"
                isYesterday(currentDate, historyDate) -> "Kemarin"
                else -> dateFormat.format(historyDate.time)
            }
            if (date != currentDateString) {
                groupedItems.add(QuizHistoryItem.DateHeader(date))
                currentDateString = date
            }
            groupedItems.add(QuizHistoryItem.QuizHistory(history))
        }

        return groupedItems
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(cal1: Calendar, cal2: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return isSameDay(yesterday, cal2)
    }
}
