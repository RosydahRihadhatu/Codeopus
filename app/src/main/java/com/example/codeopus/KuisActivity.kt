package com.example.codeopus

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.db.MyApplication
import com.example.codeopus.db.quiz.Quiz
import com.example.codeopus.db.quiz.QuizAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KuisActivity : AppCompatActivity(), QuizAdapter.OnItemClickListener {

    private lateinit var quizAdapter: QuizAdapter
    private val quizList = listOf(
        Quiz("Python", 13, 0, 13),
        Quiz("Kotlin", 10, 0, 10),
        Quiz("Dart", 7, 0, 7),
        Quiz("Java", 11, 0, 11),
        Quiz("HTML", 9, 0, 9),
        Quiz("CSS", 9, 0, 9),
        Quiz("JavaScript", 9, 0, 9)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuis)

        val backButton = findViewById<ImageButton>(R.id.btn_kembali)
        backButton.setOnClickListener {
            finish() // This will finish the current activity and take the user back to the previous screen
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.kuisrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        quizAdapter = QuizAdapter(quizList, this)
        recyclerView.adapter = quizAdapter

        loadQuizData()

        // Setup buttons and their click listeners
        val buttonHomeScreenActivity = findViewById<ImageButton>(R.id.btn_home)
        buttonHomeScreenActivity.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }

        val buttonProfilActivity = findViewById<ImageButton>(R.id.btn_profil)
        buttonProfilActivity.setOnClickListener {
            val intent = Intent(this, ProfilActivity::class.java)
            startActivity(intent)
        }

        val buttonRiwayatActivity = findViewById<ImageButton>(R.id.btn_riwayat)
        buttonRiwayatActivity.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadQuizData() {
        CoroutineScope(Dispatchers.IO).launch {
            val quizHistoryDao = MyApplication.getDatabase().quizHistoryDao()
            val quizHistories = quizHistoryDao.getAllQuizHistory()

            val updatedQuizList = quizList.map { quiz ->
                val completedTopics = quizHistories.count { it.language == quiz.title && it.score >= 10 }
                quiz.copy(progress = completedTopics)
            }

            withContext(Dispatchers.Main) {
                quizAdapter.updateData(updatedQuizList)
            }
        }
    }

    override fun onItemClick(quiz: Quiz) {
        val intent = Intent(this, TingkatanKuisActivity::class.java).apply {
            putExtra("QUIZ_TITLE", quiz.title)
        }
        startActivity(intent)
    }
}
