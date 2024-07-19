package com.example.codeopus

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import com.example.codeopus.api.ApiClient
import com.example.codeopus.api.GPTChatRequest
import com.example.codeopus.api.GPTChatResponse
import com.example.codeopus.api.Message
import com.example.codeopus.db.AppDatabase
import com.example.codeopus.db.CustomAdapter
import com.example.codeopus.db.translation.TranslationHistory
import com.example.codeopus.db.translation.TranslationHistoryDao
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TranslateActivity : AppCompatActivity() {

    private lateinit var textInputEditTextSource: TextInputEditText
    private lateinit var textInputEditTextTarget: TextInputEditText
    private var isSwapped = false

    private lateinit var spinnerSourceLanguage: Spinner
    private lateinit var spinnerTargetLanguage: Spinner

    private lateinit var database: AppDatabase
    private lateinit var translationHistoryDao: TranslationHistoryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)

        // Setup navigation buttons
        setupNavigationButtons()

        textInputEditTextSource = findViewById(R.id.textInputEditTextSource)
        textInputEditTextTarget = findViewById(R.id.textInputEditTextTarget)

        val sharedPreferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID", "") ?: ""

        // Setup database
        database = AppDatabase.getDatabase(this, userId)
        translationHistoryDao = database.translationHistoryDao()

        // Setup language spinners and switch button
        val programmingLanguages = listOf("Python", "Kotlin", "CSS", "Dart", "HTML", "JavaScript", "Java")
        val programmingIcons = listOf(
            R.drawable.python, R.drawable.kotlin, R.drawable.css, R.drawable.dart,
            R.drawable.html, R.drawable.js, R.drawable.java
        )

        val targetLanguages = listOf("Indonesia", "English(US)")
        val targetIcons = listOf(R.drawable.indonesia, R.drawable.english)

        spinnerSourceLanguage = findViewById(R.id.spinner_source_language)
        spinnerTargetLanguage = findViewById(R.id.spinner_target_language)
        val switchButton: ImageButton = findViewById(R.id.btn_switch)

        val customAdapterProgramming = CustomAdapter(this, programmingLanguages, programmingIcons)
        val customAdapterTarget = CustomAdapter(this, targetLanguages, targetIcons)

        spinnerSourceLanguage.adapter = customAdapterProgramming
        spinnerTargetLanguage.adapter = customAdapterTarget

        switchButton.setOnClickListener {
            // Swap the views
            swapViews()
            swapTextInputs()
        }

        // Ambil data dari Intent
        val codeInput = intent.getStringExtra("CODE_INPUT")
        val selectedSourceLanguageIndex = intent.getIntExtra("SOURCE_LANGUAGE_INDEX", 0)
        val selectedTargetLanguageIndex = intent.getIntExtra("TARGET_LANGUAGE_INDEX", 0)
        isSwapped = intent.getBooleanExtra("IS_SWAPPED", false)

        // Set the spinner selection to the received languages
        if (isSwapped) {
            swapViews()
            swapTextInputs()
            spinnerSourceLanguage.setSelection(selectedTargetLanguageIndex)
            spinnerTargetLanguage.setSelection(selectedSourceLanguageIndex)
            textInputEditTextSource.setText(codeInput)
            textInputEditTextTarget.setText("")
            translateToCode(codeInput ?: "", spinnerTargetLanguage.selectedItem.toString())
        } else {
            spinnerSourceLanguage.setSelection(selectedSourceLanguageIndex)
            spinnerTargetLanguage.setSelection(selectedTargetLanguageIndex)
            textInputEditTextSource.setText(codeInput)
            validateAndTranslateCode(codeInput ?: "", spinnerSourceLanguage.selectedItem.toString())
        }

        // Disable spinners to prevent changing
        spinnerSourceLanguage.isEnabled = false
        spinnerTargetLanguage.isEnabled = false

        // Setup buttons for clearing and copying text
        val btnHapus: ImageButton = findViewById(R.id.btn_hapus)
        val btnSalin: ImageButton = findViewById(R.id.btn_salin)
        val btnHapus2: ImageButton = findViewById(R.id.btn_hapus2)
        val btnSalin2: ImageButton = findViewById(R.id.btn_salin2)

        btnHapus.setOnClickListener {
            textInputEditTextSource.text?.clear()
        }

        btnSalin.setOnClickListener {
            copyToClipboard(textInputEditTextSource.text.toString())
        }

        btnHapus2.setOnClickListener {
            textInputEditTextTarget.text?.clear()
        }

        btnSalin2.setOnClickListener {
            copyToClipboard(textInputEditTextTarget.text.toString())
        }
    }

    private fun validateAndTranslateCode(code: String, language: String) {
        val selectedTargetLanguage = if (spinnerTargetLanguage.selectedItem.toString() == "Indonesia") "Indonesian" else "English"

        val validationPrompt = """
            Determine if the following code is valid $language code. 
            If it is valid, respond with "Valid $language code".
            If it is not valid, respond with "Invalid $language code".
            Here is the code:
            ```
            $code
            ```
        """.trimIndent()

        val validationMessages = listOf(
            Message(role = "system", content = "You are a code validation assistant."),
            Message(role = "user", content = validationPrompt)
        )

        val validationRequest = GPTChatRequest(
            model = "gpt-4",
            messages = validationMessages,
            max_tokens = 100,
            temperature = 0.0
        )

        ApiClient.apiService.translateCode(validationRequest).enqueue(object : Callback<GPTChatResponse> {
            override fun onResponse(call: Call<GPTChatResponse>, response: Response<GPTChatResponse>) {
                if (response.isSuccessful) {
                    val validationResult = response.body()?.choices?.firstOrNull()?.message?.content ?: "Invalid"
                    if (validationResult.contains("Valid $language code", true)) {
                        translateCode(code, language, selectedTargetLanguage)
                    } else {
                        Toast.makeText(this@TranslateActivity, "Ada Kesalahan pada Script Anda", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@TranslateActivity, "Failed to validate code", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GPTChatResponse>, t: Throwable) {
                Toast.makeText(this@TranslateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun translateCode(code: String, language: String, targetLanguage: String) {
        val prompt = """
            Briefly explain the purpose of the following $language code in one concise sentence in $targetLanguage:
            ```
            $code
            ```
        """.trimIndent()

        val messages = listOf(
            Message(role = "system", content = "You are a helpful assistant that provides concise explanations of programming code."),
            Message(role = "user", content = prompt)
        )

        val request = GPTChatRequest(
            model = "gpt-4",
            messages = messages,
            max_tokens = 50,
            temperature = 0.7
        )

        ApiClient.apiService.translateCode(request).enqueue(object : Callback<GPTChatResponse> {
            override fun onResponse(call: Call<GPTChatResponse>, response: Response<GPTChatResponse>) {
                if (response.isSuccessful) {
                    val translation = response.body()?.choices?.firstOrNull()?.message?.content ?: "No explanation found"
                    textInputEditTextTarget.setText(translation.trim())
                    saveTranslationHistory(code, translation.trim())
                    Toast.makeText(this@TranslateActivity, "Explanation successful", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@TranslateActivity, "Failed to get explanation", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GPTChatResponse>, t: Throwable) {
                Toast.makeText(this@TranslateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun translateToCode(text: String, language: String) {
        val prompt = """
            Convert the following text into $language code:
            ```
            $text
            ```
        """.trimIndent()

        val messages = listOf(
            Message(role = "system", content = "You are a helpful assistant that converts text into programming code."),
            Message(role = "user", content = prompt)
        )

        val request = GPTChatRequest(
            model = "gpt-3.5-turbo",
            messages = messages,
            max_tokens = 100,
            temperature = 0.7
        )

        ApiClient.apiService.translateCode(request).enqueue(object : Callback<GPTChatResponse> {
            override fun onResponse(call: Call<GPTChatResponse>, response: Response<GPTChatResponse>) {
                if (response.isSuccessful) {
                    val translation = response.body()?.choices?.firstOrNull()?.message?.content ?: "No code found"
                    textInputEditTextTarget.setText(translation.trim())
                    saveTranslationHistory(text, translation.trim())
                    Toast.makeText(this@TranslateActivity, "Translation successful", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@TranslateActivity, "Failed to get code", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GPTChatResponse>, t: Throwable) {
                Toast.makeText(this@TranslateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveTranslationHistory(inputText: String, translatedText: String) {
        val sharedPreferences = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID", "") ?: ""
        lifecycleScope.launch {
            // Dapatkan nomor urutan maksimum saat ini untuk pengguna ini
            val maxSequenceNumber = withContext(Dispatchers.IO) {
                translationHistoryDao.getMaxSequenceNumberByUser(userId) ?: 0
            }
            val history = TranslationHistory(
                inputText = inputText,
                translatedText = translatedText,
                timestamp = System.currentTimeMillis(),
                userId = userId,
                sequenceNumber = maxSequenceNumber + 1 // Tambahkan nomor urutan baru
            )
            withContext(Dispatchers.IO) {
                translationHistoryDao.insert(history)
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigationButtons() {
        val buttonHomeScreenActivity = findViewById<ImageButton>(R.id.btn_home_on)
        buttonHomeScreenActivity.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }
        val buttonKuisActivity = findViewById<ImageButton>(R.id.btn_kuis)
        buttonKuisActivity.setOnClickListener {
            val intent = Intent(this, KuisActivity::class.java)
            startActivity(intent)
        }

        val buttonRiwayatActivity = findViewById<ImageButton>(R.id.btn_riwayat)
        buttonRiwayatActivity.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java)
            startActivity(intent)
        }

        val buttonProfilActivity = findViewById<ImageButton>(R.id.btn_profil)
        buttonProfilActivity.setOnClickListener {
            val intent = Intent(this, ProfilActivity::class.java)
            startActivity(intent)
        }

        val buttonKembaliActivity = findViewById<ImageButton>(R.id.btn_kembali)
        buttonKembaliActivity.setOnClickListener {
            val intent = Intent(this, HomeScreenActivity::class.java)
            startActivity(intent)
        }
    }

    private fun swapViews() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint_layout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (isSwapped) {
            // Set constraints to original positions
            constraintSet.clear(R.id.spinner_source_language, ConstraintSet.START)
            constraintSet.clear(R.id.spinner_source_language, ConstraintSet.END)
            constraintSet.clear(R.id.spinner_target_language, ConstraintSet.START)
            constraintSet.clear(R.id.spinner_target_language, ConstraintSet.END)

            constraintSet.connect(R.id.spinner_source_language, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
            constraintSet.connect(R.id.spinner_source_language, ConstraintSet.END, R.id.btn_switch, ConstraintSet.START, 8)
            constraintSet.connect(R.id.spinner_target_language, ConstraintSet.START, R.id.btn_switch, ConstraintSet.END, 8)
            constraintSet.connect(R.id.spinner_target_language, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        } else {
            // Swap constraints
            constraintSet.clear(R.id.spinner_source_language, ConstraintSet.START)
            constraintSet.clear(R.id.spinner_source_language, ConstraintSet.END)
            constraintSet.clear(R.id.spinner_target_language, ConstraintSet.START)
            constraintSet.clear(R.id.spinner_target_language, ConstraintSet.END)

            constraintSet.connect(R.id.spinner_source_language, ConstraintSet.START, R.id.btn_switch, ConstraintSet.END, 8)
            constraintSet.connect(R.id.spinner_source_language, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
            constraintSet.connect(R.id.spinner_target_language, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16)
            constraintSet.connect(R.id.spinner_target_language, ConstraintSet.END, R.id.btn_switch, ConstraintSet.START, 8)
        }

        constraintSet.applyTo(constraintLayout)

        // Swap selections
        val sourceSelection = spinnerSourceLanguage.selectedItemPosition
        val targetSelection = spinnerTargetLanguage.selectedItemPosition

        spinnerSourceLanguage.setSelection(targetSelection)
        spinnerTargetLanguage.setSelection(sourceSelection)

        isSwapped = !isSwapped
    }

    private fun swapTextInputs() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint_layout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (isSwapped) {
            // Set constraints to original positions
            constraintSet.clear(R.id.textInputEditTextSource, ConstraintSet.TOP)
            constraintSet.clear(R.id.textInputEditTextTarget, ConstraintSet.TOP)

            constraintSet.connect(R.id.textInputEditTextSource, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16)
            constraintSet.connect(R.id.textInputEditTextTarget, ConstraintSet.TOP, R.id.textInputEditTextSource, ConstraintSet.BOTTOM, 16)
        } else {
            // Swap constraints
            constraintSet.clear(R.id.textInputEditTextSource, ConstraintSet.TOP)
            constraintSet.clear(R.id.textInputEditTextTarget, ConstraintSet.TOP)

            constraintSet.connect(R.id.textInputEditTextTarget, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 16)
            constraintSet.connect(R.id.textInputEditTextSource, ConstraintSet.TOP, R.id.textInputEditTextTarget, ConstraintSet.BOTTOM, 16)
        }
        constraintSet.applyTo(constraintLayout)
    }
}
