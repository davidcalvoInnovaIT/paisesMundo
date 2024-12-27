// app/src/main/java/com/david/paisesdelmundo/MainActivity.kt
package com.david.paisesdelmundo

import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.IOException
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Button
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 1
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: paisAdapter
    private var countryList: List<pais> = listOf()
    var listaReal = countryList
    val filters = mutableMapOf<String, (pais) -> Boolean>() // Mapa de filtros con identificadores
    private var lastSortOption: Int = 0
    private var isFavoriteFilterChecked: Boolean = false
    private var selectedContinentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.item_spacing)
        recyclerView.addItemDecoration(SpacingItemDecoration(spacingInPixels))

        val jsonString = loadJsonFromAssets("countries.json")
        countryList = parseJson(jsonString)?.countries ?: listOf()

        adapter = paisAdapter(countryList)
        recyclerView.adapter = adapter

        val startGameButton = findViewById<Button>(R.id.startGameButton)
        startGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("countryList", ArrayList(listaReal))
            startActivity(intent)
        }

        setupContinentSpinner()
        setupFavoriteFilter()
        setupSortSpinner()

        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }
    }

    private fun setupFavoriteFilter() {
        val switchFavorites = findViewById<Switch>(R.id.switch1)
        switchFavorites.setOnCheckedChangeListener { _, isChecked ->
            isFavoriteFilterChecked = isChecked
            if (isChecked) {
                filters["favorite"] = { it.favorite }
            } else {
                filters.remove("favorite")
            }
            applyFilters()
        }
    }

    private fun setupContinentSpinner() {
        val spinnerContinents = findViewById<Spinner>(R.id.spinnerContinents)
        ArrayAdapter.createFromResource(
            this,
            R.array.continents_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerContinents.adapter = adapter
        }

        spinnerContinents.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedContinentPosition = position
                val selectedContinent = parent.getItemAtPosition(position).toString()
                val language = Locale.getDefault().language
                val allContinents = getString(R.string.allContinents)

                if (selectedContinent == allContinents) {
                    filters.remove("continent")
                } else {
                    filters["continent"] = { country ->
                        when (language) {
                            "en" -> country.continent_en == selectedContinent
                            "es" -> country.continent_es == selectedContinent
                            else -> country.continent_en == selectedContinent // Default to English
                        }
                    }
                }
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                filters.remove("continent")
                applyFilters()
            }
        }
    }

    private fun applyFilters() {
        // Filtrar la lista
        listaReal = countryList.filter { item -> filters.values.all { filter -> filter(item) } }

        // Ordenar la lista según la última opción seleccionada
        listaReal = when (lastSortOption) {
            0 -> listaReal.sortedBy { if (Locale.getDefault().language == "es") it.name_es else it.name_en }
            1 -> listaReal.sortedByDescending { if (Locale.getDefault().language == "es") it.name_es else it.name_en }
            2 -> listaReal.sortedBy { if (Locale.getDefault().language == "es") it.capital_es else it.capital_en }
            3 -> listaReal.sortedByDescending { if (Locale.getDefault().language == "es") it.capital_es else it.capital_en }
            else -> listaReal
        }

        // Actualizar el adaptador
        adapter.updateList(listaReal)
    }

    private fun loadJsonFromAssets(filename: String): String? {
        return try {
            val inputStream = assets.open(filename)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    private fun parseJson(jsonString: String?): listaPais? {
        return jsonString?.let {
            Gson().fromJson(it, listaPais::class.java)
        }
    }

    private fun setupSortSpinner() {
        val spinnerSort = findViewById<Spinner>(R.id.spinnerSort)
        ArrayAdapter.createFromResource(
            this,
            R.array.sort_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSort.adapter = adapter
        }

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Guardar la opción seleccionada
                lastSortOption = position

                // Reaplicar filtros (lo que también aplicará la ordenación)
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("lastSortOption", lastSortOption)
        outState.putBoolean("isFavoriteFilterChecked", isFavoriteFilterChecked)
        outState.putInt("selectedContinentPosition", selectedContinentPosition)
        outState.putString("countryList", Gson().toJson(countryList))
    }

    private fun restoreState(savedInstanceState: Bundle) {
        lastSortOption = savedInstanceState.getInt("lastSortOption", 0)
        isFavoriteFilterChecked = savedInstanceState.getBoolean("isFavoriteFilterChecked", false)
        selectedContinentPosition = savedInstanceState.getInt("selectedContinentPosition", 0)
        val countryListJson = savedInstanceState.getString("countryList")
        countryList = Gson().fromJson(countryListJson, Array<pais>::class.java).toList()
        applyFilters()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val updatedCountry = data?.getSerializableExtra("country") as? pais
            updatedCountry?.let { country ->
                val index = countryList.indexOfFirst { it.code_3 == country.code_3 }
                if (index != -1) {
                    countryList = countryList.toMutableList().apply {
                        this[index] = country
                    }
                    applyFilters()
                }
            }
        }
    }
}