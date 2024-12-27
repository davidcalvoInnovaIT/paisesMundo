// app/src/main/java/com/david/paisesdelmundo/paisAdapter.kt
package com.david.paisesdelmundo

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class paisAdapter(private var countries: List<pais>) :
    RecyclerView.Adapter<paisAdapter.CountryViewHolder>() {

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmoji: TextView = itemView.findViewById(R.id.tvEmoji)
        val tvCountryName: TextView = itemView.findViewById(R.id.tvCountryName)
        val tvCapital: TextView = itemView.findViewById(R.id.tvCapital)
        val tvContinent: TextView = itemView.findViewById(R.id.tvContinent)
        val tvArea: TextView = itemView.findViewById(R.id.tvArea)
        val btnFavorite: ImageButton = itemView.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lista, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]
        val language = Locale.getDefault().language

        holder.tvEmoji.text = country.emoji

        if (language == "en") {
            holder.tvCountryName.text = "${country.name_en}"
            holder.tvCapital.text = "Capital: ${country.capital_en}"
            holder.tvContinent.text = "Continent: ${country.continent_en}"
            holder.tvArea.text = "Area: ${country.km2} km²"
        } else {
            holder.tvCountryName.text = "${country.name_es}"
            holder.tvCapital.text = "Capital: ${country.capital_es}"
            holder.tvContinent.text = "Continente: ${country.continent_es}"
            holder.tvArea.text = "Área: ${country.km2} km²"
        }

        if (country.km2 > 1_000_000) {
            holder.tvArea.setTypeface(null, Typeface.BOLD)
        } else {
            holder.tvArea.setTypeface(null, Typeface.NORMAL)
        }
        holder.tvEmoji.setOnClickListener {
            val countryName = if (language == "en") country.name_en else country.name_es
            val url = "https://es.wikipedia.org/wiki/$countryName"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            holder.itemView.context.startActivity(intent)
        }

        holder.btnFavorite.setImageResource(
            if (country.favorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        )

        holder.btnFavorite.setOnClickListener {
            country.favorite = !country.favorite
            notifyItemChanged(position)
        }

        val backgroundColor = when (country.continent_es) {
            "Europa" -> R.color.colorEurope
            "África" -> R.color.colorAfrica
            "Oceanía" -> R.color.colorOceania
            "América del Norte" -> R.color.colorNorthAmerica
            "América del Sur" -> R.color.colorSouthAmerica
            "Antártida" -> R.color.colorAntartica
            "Asia" -> R.color.colorAsia
            else -> android.R.color.transparent
        }
        holder.itemView.setBackgroundResource(backgroundColor)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CountryDetailActivity::class.java)
            intent.putExtra("country", country)
            (holder.itemView.context as MainActivity).startActivityForResult(intent, MainActivity.REQUEST_CODE)
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    fun filterByAfrica() {
        val filteredList = countries.filter { it.continent_en == "Africa" }
        updateList(filteredList)
    }

    fun updateList(newCountries: List<pais>) {
        countries = newCountries
        notifyDataSetChanged()
    }
}