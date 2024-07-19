package com.example.codeopus

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codeopus.db.MyApplication
import com.example.codeopus.db.SoalJawaban
import com.example.codeopus.db.quiz.QuizHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SoalActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var currentQuestionTextView: TextView
    private lateinit var scriptTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var ansA: Button
    private lateinit var ansB: Button
    private lateinit var ansC: Button
    private lateinit var ansD: Button
    private lateinit var submitBtn: ImageButton

    private var score = 0
    private lateinit var questions: List<String>
    private lateinit var choices: List<List<String>>
    private lateinit var correctAnswers: List<String>
    private lateinit var scripts: List<String>
    private var totalQuestion = 0
    private var currentQuestionIndex = 0
    private var selectedAnswer: String? = null

    private lateinit var quizLanguage: String
    private lateinit var quizLevel: String
    private lateinit var quizTopic: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soal)

        currentQuestionTextView = findViewById(R.id.current_question)
        questionTextView = findViewById(R.id.question)
        scriptTextView = findViewById(R.id.script)
        ansA = findViewById(R.id.ans_A)
        ansB = findViewById(R.id.ans_B)
        ansC = findViewById(R.id.ans_C)
        ansD = findViewById(R.id.ans_D)
        submitBtn = findViewById(R.id.submit_btn)

        ansA.setOnClickListener(this)
        ansB.setOnClickListener(this)
        ansC.setOnClickListener(this)
        ansD.setOnClickListener(this)
        submitBtn.setOnClickListener(this)

        findViewById<ImageButton>(R.id.btn_keluar).setOnClickListener {
            showExitDialog()
        }

        // Mengambil bahasa, level, dan topik dari Intent
        quizLanguage = intent.getStringExtra("QUIZ_LANGUAGE") ?: "Kotlin"
        quizLevel = intent.getStringExtra("QUIZ_LEVEL") ?: "Intermediate"
        quizTopic = intent.getStringExtra("QUIZ_TOPIC") ?: "Unknown Topic"

        loadQuestions(quizLanguage, quizLevel, quizTopic)

        if (questions.isEmpty()) {
            Toast.makeText(this, "Tidak ada soal untuk topik ini.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        totalQuestion = questions.size
        updateCurrentQuestion()
        loadNewQuestion()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ans_A, R.id.ans_B, R.id.ans_C, R.id.ans_D -> {
                resetButtonColors()

                val clickedButton = view as Button
                selectedAnswer = clickedButton.text.toString()
                clickedButton.setBackgroundColor(Color.WHITE)
                clickedButton.setTextColor(Color.BLUE)
            }
            R.id.submit_btn -> {
                if (selectedAnswer == null) {
                    showUnansweredDialog()
                } else {
                    if (selectedAnswer == correctAnswers[currentQuestionIndex]) {
                        score++
                    }

                    // Reset selectedAnswer for the next question
                    selectedAnswer = null

                    // Load next question
                    currentQuestionIndex++
                    if (currentQuestionIndex < totalQuestion) {
                        updateCurrentQuestion()
                        loadNewQuestion()
                        resetButtonColors()
                    } else {
                        finishQuiz()
                    }
                }
            }
        }
    }

    private fun resetButtonColors() {
        ansA.setBackgroundResource(R.drawable.kotak_jawaban)
        ansB.setBackgroundResource(R.drawable.kotak_jawaban)
        ansC.setBackgroundResource(R.drawable.kotak_jawaban)
        ansD.setBackgroundResource(R.drawable.kotak_jawaban)

        ansA.setTextColor(Color.WHITE)
        ansB.setTextColor(Color.WHITE)
        ansC.setTextColor(Color.WHITE)
        ansD.setTextColor(Color.WHITE)
    }

    private fun loadNewQuestion() {
        questionTextView.text = questions[currentQuestionIndex]
        ansA.text = choices[currentQuestionIndex][0]
        ansB.text = choices[currentQuestionIndex][1]
        ansC.text = choices[currentQuestionIndex][2]
        ansD.text = choices[currentQuestionIndex][3]

        val script = scripts.getOrNull(currentQuestionIndex)
        if (script != null) {
            scriptTextView.visibility = View.VISIBLE
            scriptTextView.text = script
        } else {
            scriptTextView.visibility = View.GONE
        }
    }

    private fun updateCurrentQuestion() {
        currentQuestionTextView.text = "${currentQuestionIndex + 1}/$totalQuestion"
    }

    private fun showUnansweredDialog() {
        AlertDialog.Builder(this)
            .setTitle("Soal Tidak Terjawab")
            .setMessage("Anda belum memilih jawaban. Silakan pilih jawaban sebelum melanjutkan.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun showExitDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        dialogView.findViewById<Button>(R.id.btn_tidak).setOnClickListener {
            alertDialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.btn_ya).setOnClickListener {
            alertDialog.dismiss()
            finish()
        }

        alertDialog.show()
    }

    private fun finishQuiz() {
        saveQuizResult(quizLanguage, quizLevel, quizTopic)  // Gunakan bahasa, level, dan topik yang diambil dari Intent

        val intent = if (score >= 10) {
            Intent(this, HasilJawabanBerhasilActivity::class.java)
        } else {
            Intent(this, HasilJawabanGagalActivity::class.java)
        }
        intent.putExtra("SCORE", score)
        intent.putExtra("TOTAL_QUESTIONS", totalQuestion)
        startActivity(intent)
        finish()
    }

    private fun saveQuizResult(language: String, level: String, topic: String) {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val category = "$language > $level"

        val quizHistory = QuizHistory(
            title = topic,
            date = date,
            score = score,
            total = totalQuestion,
            category = category,
            language = language,
            level = level
        )

        CoroutineScope(Dispatchers.IO).launch {
            MyApplication.getDatabase().quizHistoryDao().insertQuizHistory(quizHistory)
            println("Quiz result saved: $quizHistory") // Log ini untuk memastikan data disimpan
        }
    }

    private fun loadQuestions(language: String, level: String, title: String) {
        questions = SoalJawaban.getQuestions(language, level, title)
        choices = SoalJawaban.getChoices(language, level, title)
        correctAnswers = SoalJawaban.getCorrectAnswers(language, level, title)
        scripts = SoalJawaban.getScripts(language, level, title)
    }
}
