package com.example.codeopus

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: LoginHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = LoginHelper(this)

        val etUsername: EditText = findViewById(R.id.username)
        val etPassword: EditText = findViewById(R.id.password)
        val etConfirmPassword: EditText = findViewById(R.id.confirmpassword)
        val btnRegister: ImageView = findViewById(R.id.btn_register)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Nama dan Kata Sandi Masih Kosong", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Kata Sandi Tidak Sama", Toast.LENGTH_SHORT).show()
            } else if (dbHelper.checkUsername(username)) {
                Toast.makeText(this, "Nama Telah Digunakan" , Toast.LENGTH_SHORT).show()
            } else {
                val success = dbHelper.addUser(username, password)
                if (success > 0) {
                    Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                    // Redirect to LoginActivity after successful registration
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val buttonLoginActivity = findViewById<TextView>(R.id.btn_login)
        buttonLoginActivity.setOnClickListener {
            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
