package com.example.codeopus.riwayat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.example.codeopus.db.AppDatabase
import com.example.codeopus.R
import com.example.codeopus.db.translation.TranslationHistory
import com.example.codeopus.db.translation.TranslationHistoryAdapter
import com.example.codeopus.db.translation.TranslationHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FragmentRiwayatTerjemahanActivity : Fragment() {

    private lateinit var database: AppDatabase
    private lateinit var translationHistoryDao: TranslationHistoryDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TranslationHistoryAdapter
    private var historyList: List<TranslationHistory> = listOf()
    private var filteredHistoryList: List<TranslationHistory> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_riwayat_terjemahan_fragment, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_history)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TranslationHistoryAdapter(historyList)
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterHistory(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterHistory(newText)
                return false
            }
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("USER_ID", "") ?: ""
        database = AppDatabase.getDatabase(requireContext(), userId)
        translationHistoryDao = database.translationHistoryDao()
        loadHistory(userId)
    }

    private fun loadHistory(userId: String) {
        lifecycleScope.launch {
            try {
                historyList = withContext(Dispatchers.IO) {
                    translationHistoryDao.getAllHistoryByUser(userId)
                }
                Log.d("FragmentRiwayat", "Loaded history: ${historyList.size} items")
                filteredHistoryList = historyList
                adapter.updateData(filteredHistoryList)
            } catch (e: Exception) {
                Log.e("FragmentRiwayat", "Error loading history", e)
            }
        }
    }

    private fun filterHistory(query: String?) {
        Log.d("FragmentRiwayat", "Filtering history with query: $query")
        val filteredList = if (query.isNullOrEmpty()) {
            historyList
        } else {
            historyList.filter {
                val timestampFormatted = SimpleDateFormat("yyyyMMdd-HHmm", Locale.getDefault()).format(Date(it.timestamp)
                )
                it.inputText.contains(query, ignoreCase = true) ||
                        it.translatedText.contains(query, ignoreCase = true) ||
                        timestampFormatted.contains(query, ignoreCase = true)
            }
        }
        Log.d("FragmentRiwayat", "Filtered history: ${filteredList.size} items")
        filteredHistoryList = filteredList
        adapter.updateData(filteredHistoryList)
    }
}
