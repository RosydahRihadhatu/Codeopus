package com.example.codeopus

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HasilJawabanGagalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_jawaban_gagal)

        val scoreTextView: TextView = findViewById(R.id.scoreTextView)
        val score = intent.getIntExtra("SCORE", 0)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)
        scoreTextView.text = "$score/$totalQuestions"

        // Close button to end the session and navigate to the quiz selection screen
        val closeButton = findViewById<ImageButton>(R.id.btn_silang)
        closeButton.setOnClickListener {
            val intent = Intent(this, KuisActivity::class.java) // Change this to your quiz selection activity
            startActivity(intent)
            finish()
        }
    }
}
