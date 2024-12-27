// app/src/main/java/com/david/paisesdelmundo/CountryDetailActivity.kt
package com.david.paisesdelmundo

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class CountryDetailActivity : AppCompatActivity() {

    private lateinit var country: pais
    private lateinit var tvCountryName: TextView
    private lateinit var tvCapital: TextView
    private lateinit var tvContinent: TextView
    private lateinit var tvArea: TextView
    private lateinit var tvEmoji: TextView
    private lateinit var btnFavorite: ImageButton
    private lateinit var numCode: TextView
    private lateinit var code2: TextView
    private lateinit var code3: TextView
    private lateinit var tld: TextView
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_detail)

        country = intent.getSerializableExtra("country") as pais

        tvCountryName = findViewById(R.id.tvCountryName)
        tvCapital = findViewById(R.id.tvCapital)
        tvContinent = findViewById(R.id.tvContinent)
        tvArea = findViewById(R.id.tvArea)
        tvEmoji = findViewById(R.id.tvEmoji)
        btnFavorite = findViewById(R.id.btnFavorite)
        numCode = findViewById(R.id.tvNumCode)
        code2 = findViewById(R.id.code2)
        code3 = findViewById(R.id.code3)
        tld = findViewById(R.id.tld)
        btnBack = findViewById(R.id.btnBack)

        val language = Locale.getDefault().language

        if (language == "es") {
            tvCountryName.text = country.name_es
            tvCapital.text = "Capital: ${country.capital_es}"
            tvContinent.text = "Continente: ${country.continent_es}"
            tvArea.text = "Área: ${country.km2} km²"
            numCode.text = "Código numérico: ${country.dial_code}"
            code2.text = "Código 2: ${country.code_2}"
            code3.text = "Código 3: ${country.code_3}"
            tld.text = "Dominio de nivel superior: ${country.tld}"
        } else {
            tvCountryName.text = country.name_en
            tvCapital.text = "Capital: ${country.capital_en}"
            tvContinent.text = "Continent: ${country.continent_en}"
            tvArea.text = "Area: ${country.km2} km²"
            numCode.text = "Numeric code: ${country.dial_code}"
            code2.text = "Code 2: ${country.code_2}"
            code3.text = "Code 3: ${country.code_3}"
            tld.text = "Top Level Domain: ${country.tld}"
        }

        tvEmoji.text = country.emoji

        updateFavoriteButton()

        btnFavorite.setOnClickListener {
            country.favorite = !country.favorite
            updateFavoriteButton()
            setResult(RESULT_OK, intent.putExtra("country", country))
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateFavoriteButton() {
        btnFavorite.setImageResource(
            if (country.favorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        )
    }
}