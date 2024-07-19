package com.example.codeopus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codeopus.db.MyApplication

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: LoginHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = LoginHelper(this)

        val etUsername: EditText = findViewById(R.id.username)
        val etPassword: EditText = findViewById(R.id.password)
        val btnLogin: ImageView = findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi Nama dan Kata Sandi!", Toast.LENGTH_SHORT).show()
            } else {
                val userExists = dbHelper.checkUser(username, password)
                if (userExists) {
                    Toast.makeText(this, "Masuk Berhasil", Toast.LENGTH_SHORT).show()
                    // Simpan userId di SharedPreferences
                    saveUserToSharedPreferences(username)
                    // Inisialisasi database untuk user ini
                    MyApplication.initializeDatabase(username, this)
                    // Arahkan ke aktivitas berikutnya
                    val intent = Intent(this, HomeScreenActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Nama atau Kata Sandi tidak Valid", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val buttonRegisterActivity = findViewById<TextView>(R.id.btn_register)
        buttonRegisterActivity.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserToSharedPreferences(username: String) {
        val sharedPreferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USERNAME", username)
        editor.apply()
    }
}
