package com.example.codeopus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codeopus.db.ChildItem
import com.example.codeopus.db.HeaderItem
import com.example.codeopus.db.MyAdapter
import com.example.codeopus.db.MyApplication
import com.example.codeopus.db.quiz.QuizHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TingkatanKuisActivity : AppCompatActivity() {

    private lateinit var headers: List<HeaderItem>
    private lateinit var quizTitle: String
    private var quizHistory: List<QuizHistory> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tingkatan_kuis)

        val backButton = findViewById<ImageButton>(R.id.btn_kembali)
        backButton.setOnClickListener {
            finish()
        }

        quizTitle = intent.getStringExtra("QUIZ_TITLE") ?: "Unknown Quiz"
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = quizTitle

        headers = loadHeaders(quizTitle)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter(headers, quizTitle, quizHistory) { childItem ->
            val canAttempt = canAttemptChildItem(childItem)
            if (canAttempt) {
                val intent = Intent(this, SoalActivity::class.java).apply {
                    putExtra("QUIZ_LANGUAGE", quizTitle)
                    putExtra("QUIZ_LEVEL", childItem.level)
                    putExtra("QUIZ_TOPIC", childItem.title)
                }
                startActivityForResult(intent, 1)
            } else {
                Toast.makeText(this, "Kerjakan Soal Sebelumnya!", Toast.LENGTH_LONG).show()
            }
        }

        loadPassedQuizzes(quizTitle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val completedItem = data?.getStringExtra("COMPLETED_ITEM")
            val score = data?.getIntExtra("SCORE", 0) ?: 0
            if (completedItem != null) {
                updateQuizHistory(completedItem, score)
            }
        }
    }

    private fun loadHeaders(quizTitle: String): List<HeaderItem> {
        return when (quizTitle) {
            "Python" -> listOf(
                HeaderItem("Basic", listOf(
                    ChildItem("Tipe Data dan Variabel", "basic"),
                    ChildItem("Tipe Data List", "basic"),
                    ChildItem("Tipe Data Tupple", "basic"),
                    ChildItem("Input dan Output", "basic"),
                    ChildItem("Percabangan", "basic"),
                    ChildItem("Perulangan", "basic")
                )),
                HeaderItem("Intermediate", listOf(
                    ChildItem("Fungsi", "intermediate"),
                    ChildItem("Recursive Function", "intermediate"),
                    ChildItem("Lambda Function", "intermediate"),
                    ChildItem("Recursive Function", "intermediate")
                )),
                HeaderItem("Advanced", listOf(
                    ChildItem("Object Oriented Programming:", "advanced"),
                    ChildItem("Generator", "advanced"),
                    ChildItem("Modul Library", "advanced")
                ))
            )
            "Kotlin" -> listOf(
                HeaderItem("Basic", listOf(
                    ChildItem("Activity", "basic"),
                    ChildItem("Intent", "basic"),
                    ChildItem("View dan ViewGroup", "basic"),
                    ChildItem("Style dan Theme", "basic"),
                    ChildItem("Recycler View", "basic")
                )),
                HeaderItem("Intermediate", listOf(
                    ChildItem("Android Widget", "intermediate"),
                    ChildItem("Motion Layout", "intermediate"),
                    ChildItem("Adaptive Layout", "intermediate"),
                )),
                HeaderItem("Advanced", listOf(
                    ChildItem("Database", "advanced"),
                    ChildItem("Firebase dan Reflection", "advanced")
                ))
            )
            "Dart" -> listOf(
                HeaderItem("Basic", listOf(
                    ChildItem("Fungsi Input dan Output", "basic"),
                    ChildItem("Operator", "basic")
                )),
                HeaderItem("Intermediate", listOf(
                    ChildItem("Control Flow (Percangan)", "intermediate"),
                    ChildItem("Control Flow (Perulangan)", "intermediate"),
                    ChildItem("Struktur data List", "intermediate")
                )),
                HeaderItem("Advanced", listOf(
                    ChildItem("Generics", "advanced"),
                    ChildItem("Advanced Asynchronous Programming", "advanced")
                ))
            )
            "Java" -> listOf(
                HeaderItem("Basic", listOf(
                    ChildItem("Variabel dan Tipe Data", "basic"),
                    ChildItem("Operator", "basic"),
                    ChildItem("Percabangan", "basic"),
                    ChildItem("Perulangan", "basic"),
                    ChildItem("Struktur Data Array", "basic")
                )),
                HeaderItem("Intermediate", listOf(
                    ChildItem("Datastructures", "intermediate"),
                    ChildItem("OOP, Interfaces, Classes", "intermediate"),
                    ChildItem("Package", "intermediate")
                )),
                HeaderItem("Advanced", listOf(
                    ChildItem("Memory Management", "advanced"),
                    ChildItem("Collection Framework", "advanced"),
                    ChildItem("Serialization", "advanced")
                ))
            )
            "HTML" -> listOf(
                HeaderItem("Basic", listOf(
                    ChildItem("Elemen dan Tag", "basic"),
                    ChildItem("Atribut", "basic"),
                    ChildItem("Link dan Gambar", "basic")
                )),
                HeaderItem("Intermediate", listOf(
                    ChildItem("Audio dan Video", "intermediate"),
                    ChildItem("Layout", "intermediate"),
                    ChildItem("Tabel", "intermediate")
                )),
                HeaderItem("Advanced", listOf(
                    ChildItem("Canvas dan SVG", "advanced"),
                    ChildItem("Collecting Information", "advanced"),
                    ChildItem("Microdata dan Aksesibilitas", "advanced")
                ))
            )
            "CSS" -> listOf(
                HeaderItem("Basic", listOf(
                    ChildItem("Selektor CSS", "basic"),
                    ChildItem("Margin dan Padding", "basic"),
                    ChildItem("Position CSS", "basic"),
                    ChildItem("Border CSS", "basic"),
                    ChildItem("Properti Font dan Warna", "basic")
                )),
                HeaderItem("Intermediate", listOf(
                    ChildItem("Grid Layout", "intermediate"),
                    ChildItem("Flexbox", "intermediate")
                )),
                HeaderItem("Advanced", listOf(
                    ChildItem("CSS Frameworks", "advanced"),
                    ChildItem("Performance Optimization", "advanced")
                ))
            )
            else -> listOf(
                HeaderItem("Basic", listOf(
                    ChildItem("Dasar-dasar JavaScript", "basic"),
                    ChildItem("Tipe Operator", "basic"),
                    ChildItem("Deklarasi Variabel", "basic"),
                    ChildItem("JSON", "basic")
                )),
                HeaderItem("Intermediate", listOf(
                    ChildItem("Argumen Objek", "intermediate"),
                    ChildItem("Rekursi", "intermediate"),
                    ChildItem("Asynchronous JavaScript", "intermediate"),
                )),
                HeaderItem("Advanced", listOf(
                    ChildItem("Memori manajemen", "advanced"),
                    ChildItem("Debugging", "advanced")
                ))
            )
        }
    }

    private fun canAttemptChildItem(childItem: ChildItem): Boolean {
        val flatChildItems = headers.flatMap { it.childItems }
        val currentIndex = flatChildItems.indexOf(childItem)

        // Allow retrying the current quiz regardless of score
        val currentQuiz = quizHistory.find { it.title == childItem.title && it.level == childItem.level }
        if (currentQuiz != null) {
            return true
        }

        // Allow accessing the current quiz if it is the first item
        if (currentIndex == 0) return true

        // Allow accessing the current quiz if the previous quiz has a score of 10 or more
        if (currentIndex > 0) {
            val previousItem = flatChildItems[currentIndex - 1]
            val previousQuiz = quizHistory.find { it.title == previousItem.title && it.level == previousItem.level }
            return previousQuiz?.score ?: 0 >= 10
        }

        return false
    }

    private fun loadPassedQuizzes(language: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val passedQuizzesList = MyApplication.getDatabase().quizHistoryDao().getAllQuizHistory()
            quizHistory = passedQuizzesList.filter { it.language == language }

            val updatedHeaders = headers.map { header ->
                val children = header.childItems.map { child ->
                    val quiz = quizHistory.find { it.title == child.title && it.level == child.level }
                    if (quiz != null) {
                        child.copy(isDone = true, score = quiz.score, totalScore = quiz.total)
                    } else {
                        child
                    }
                }
                header.copy(childItems = children)
            }

            withContext(Dispatchers.Main) {
                findViewById<RecyclerView>(R.id.recyclerView).adapter = MyAdapter(updatedHeaders, language, quizHistory) { childItem ->
                    val canAttempt = canAttemptChildItem(childItem)
                    if (canAttempt) {
                        val intent = Intent(this@TingkatanKuisActivity, SoalActivity::class.java).apply {
                            putExtra("QUIZ_LANGUAGE", language)
                            putExtra("QUIZ_LEVEL", childItem.level)
                            putExtra("QUIZ_TOPIC", childItem.title)
                        }
                        startActivityForResult(intent, 1)
                    } else {
                        Toast.makeText(this@TingkatanKuisActivity, "Kerjakan Soal Sebelumnya!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateQuizHistory(completedItem: String, score: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val quiz = quizHistory.find { it.title == completedItem }
            if (quiz != null) {
                val updatedQuiz = quiz.copy(score = score)
                quizHistory = quizHistory - quiz + updatedQuiz
                MyApplication.getDatabase().quizHistoryDao().updateQuizHistory(updatedQuiz)
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.format(Date())
                val newQuiz = QuizHistory(
                    title = completedItem,
                    language = quizTitle,
                    level = "Unknown Level",
                    date = date,
                    score = score,
                    total = 10,
                    category = "$quizTitle > Unknown Level"
                )
                quizHistory = quizHistory + newQuiz
                MyApplication.getDatabase().quizHistoryDao().insertQuizHistory(newQuiz)
            }

            withContext(Dispatchers.Main) {
                findViewById<RecyclerView>(R.id.recyclerView).adapter?.notifyDataSetChanged()
            }
        }
    }
}
