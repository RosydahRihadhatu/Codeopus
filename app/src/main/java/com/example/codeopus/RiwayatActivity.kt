package com.example.codeopus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.codeopus.riwayat.FragmentRiwayatKuisActivity
import com.example.codeopus.riwayat.FragmentRiwayatTerjemahanActivity


class RiwayatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat)

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

        val buttonKuisActivity = findViewById<ImageButton>(R.id.btn_kuis)
        buttonKuisActivity.setOnClickListener {
            val intent = Intent(this, KuisActivity::class.java)
            startActivity(intent)
        }

        // Tampilkan fragment RiwayatTerjemahan saat Activity pertama kali dibuka
        if (savedInstanceState == null) {
            replaceFragment(FragmentRiwayatTerjemahanActivity())
        }

        findViewById<Button>(R.id.btn_riwayat_terjemahan).setOnClickListener {
            replaceFragment(FragmentRiwayatTerjemahanActivity())
        }

        findViewById<Button>(R.id.btn_riwayat_kuis).setOnClickListener {
            replaceFragment(FragmentRiwayatKuisActivity())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_riwayat, fragment)
            .commit()
    }
}
