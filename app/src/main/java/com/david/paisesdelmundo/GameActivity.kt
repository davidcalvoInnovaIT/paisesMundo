// app/src/main/java/com/david/paisesdelmundo/GameActivity.kt
package com.david.paisesdelmundo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class GameActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var optionButtons: List<Button>
    private lateinit var scoreTextView: TextView
    private lateinit var highScoreTextView: TextView

    private var currentCountry: pais? = null
    private var score: Int = 0
    private var highScore: Int = 0
    private var countryList: List<pais> = listOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
        questionTextView = findViewById(R.id.questionTextView)
        optionButtons = listOf(
            findViewById(R.id.optionButton1),
            findViewById(R.id.optionButton2),
            findViewById(R.id.optionButton3),
            findViewById(R.id.optionButton4)
        )
        scoreTextView = findViewById(R.id.scoreTextView)
        highScoreTextView = findViewById(R.id.highScoreTextView)

        countryList = intent.getSerializableExtra("countryList") as List<pais>

        loadHighScore()
        askQuestion()
    }

    private fun askQuestion() {
        if (countryList.isEmpty()) return

        currentCountry = countryList.random()
        val correctCapital = currentCountry?.capital_es ?: ""
        val options = mutableListOf(correctCapital)

        while (options.size < 4) {
            val randomCapital = countryList.random().capital_es
            if (randomCapital !in options) {
                options.add(randomCapital)
            }
        }

        options.shuffle()

        val language = Locale.getDefault().language
        if (language == "es") {
            questionTextView.text = "¿Cuál es la capital de ${currentCountry?.name_es}?"
            scoreTextView.text = "Aciertos: $score"
            highScoreTextView.text = "Puntuación más alta: $highScore"
        } else {
            questionTextView.text = "What is the capital of ${currentCountry?.name_en}?"
            scoreTextView.text = "Score: $score"
            highScoreTextView.text = "High Score: $highScore"
        }

        optionButtons.forEachIndexed { index, button ->
            button.text = options[index]
            button.setBackgroundColor(resources.getColor(android.R.color.darker_gray)) // Reset button color
            button.setOnClickListener { checkAnswer(button, button.text.toString()) }
        }
    }

    private fun checkAnswer(selectedButton: Button, selectedAnswer: String) {
        val correctAnswer = currentCountry?.capital_es
        if (selectedAnswer == correctAnswer) {
            selectedButton.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
            score++
            val language = Locale.getDefault().language
            if (language == "es") {
                scoreTextView.text = "Aciertos: $score"
            } else {
                scoreTextView.text = "Score: $score"
            }
            Handler(Looper.getMainLooper()).postDelayed({
                askQuestion()
            }, 500)
        } else {
            val language = Locale.getDefault().language
            if (language == "es") {
                Toast.makeText(this, "Incorrecto! La capital correcta es $correctAnswer", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Incorrect! The correct capital is $correctAnswer", Toast.LENGTH_LONG).show()
            }
            if (score > highScore) {
                highScore = score
                saveHighScore()
            }
            finish()
        }
    }

    private fun loadHighScore() {
        val sharedPreferences = getSharedPreferences("game_prefs", MODE_PRIVATE)
        highScore = sharedPreferences.getInt("high_score", 0)
        val language = Locale.getDefault().language
        if (language == "es") {
            highScoreTextView.text = "Puntuación más alta: $highScore"
        } else {
            highScoreTextView.text = "High Score: $highScore"
        }
    }

    private fun saveHighScore() {
        val sharedPreferences = getSharedPreferences("game_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("high_score", highScore)
            apply()
        }
    }
}