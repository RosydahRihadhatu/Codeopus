package com.example.codeopus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.codeopus.bahasaprofil.FragmentCssActivity
import com.example.codeopus.bahasaprofil.FragmentDartActivity
import com.example.codeopus.bahasaprofil.FragmentHtmlActivity
import com.example.codeopus.bahasaprofil.FragmentJavaActivity
import com.example.codeopus.bahasaprofil.FragmentJavascriptActivity
import com.example.codeopus.bahasaprofil.FragmentKotlinActivity
import com.example.codeopus.bahasaprofil.FragmentPythonActivity

class ProfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        setupNavigationButtons()

        val sharedPreferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("USERNAME", "Guest")

        val tvUsername = findViewById<TextView>(R.id.txt_username)
        tvUsername.text = "$username"
        // Initialize fragments and set default fragment
        replaceFragment(FragmentPythonActivity())

        findViewById<TextView>(R.id.txt_python_profil).setOnClickListener {
            replaceFragment(FragmentPythonActivity())
        }
        findViewById<TextView>(R.id.txt_kotlin_profil).setOnClickListener {
            replaceFragment(FragmentKotlinActivity())
        }
        findViewById<TextView>(R.id.txt_dart_profil).setOnClickListener {
            replaceFragment(FragmentDartActivity())
        }
        findViewById<TextView>(R.id.txt_java_profil).setOnClickListener {
            replaceFragment(FragmentJavaActivity())
        }
        findViewById<TextView>(R.id.txt_html_profil).setOnClickListener {
            replaceFragment(FragmentHtmlActivity())
        }
        findViewById<TextView>(R.id.txt_css_profil).setOnClickListener {
            replaceFragment(FragmentCssActivity())
        }
        findViewById<TextView>(R.id.txt_javascript_profil).setOnClickListener {
            replaceFragment(FragmentJavascriptActivity())
        }

        findViewById<ImageView>(R.id.btn_logout).setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()

        val dialogYesButton = dialogView.findViewById<TextView>(R.id.btn_ya)
        val dialogNoButton = dialogView.findViewById<TextView>(R.id.btn_tidak)

        dialogYesButton.setOnClickListener {
            logout()
            alertDialog.dismiss()
        }

        dialogNoButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun logout() {
        // Clear user session
        UserSession.clearSession(this)

        // Intent untuk mengarahkan ke LoginActivity dan meng-clear semua previous activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Tampilkan toast
        Toast.makeText(this, "Anda berhasil keluar", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigationButtons() {
        val buttonHomeScreenActivity = findViewById<ImageButton>(R.id.btn_home)
        buttonHomeScreenActivity.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }

        val buttonRiwayatActivity = findViewById<ImageButton>(R.id.btn_riwayat)
        buttonRiwayatActivity.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            startActivity(intent)
        }

        val buttonKuisActivity = findViewById<ImageButton>(R.id.btn_kuis)
        buttonKuisActivity.setOnClickListener {
            val intent = Intent(this, KuisActivity::class.java)
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_profil, fragment)
            .commit()
    }
}
