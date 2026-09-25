package com.movflix.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val id = intent.getIntExtra("id", 0)
        val type = intent.getStringExtra("type") ?: "movie"
        val fallbackTitle = intent.getStringExtra("title") ?: ""

        val backdrop: ImageView = findViewById(R.id.detailBackdrop)
        val titleView: TextView = findViewById(R.id.detailTitle)
        val ratingView: TextView = findViewById(R.id.detailRating)
        val yearView: TextView = findViewById(R.id.detailYear)
        val genresView: TextView = findViewById(R.id.detailGenres)
        val overviewView: TextView = findViewById(R.id.detailOverview)
        val playBtn: MaterialButton = findViewById(R.id.playBtn)
        val moreBtn: MaterialButton = findViewById(R.id.moreBtn)

        titleView.text = fallbackTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Api.service.detail(type, id).enqueue(object : Callback<Detail> {
            override fun onResponse(call: Call<Detail>, response: Response<Detail>) {
                val d = response.body()
                if (d == null) {
                    Toast.makeText(this@DetailActivity, "Detail load nahi hua", Toast.LENGTH_SHORT).show()
                    return
                }
                backdrop.load(Api.img(d.backdropPath ?: d.posterPath, "w780")) {
                    placeholder(R.color.card_bg)
                    error(R.color.card_bg)
                }
                titleView.text = d.displayTitle
                ratingView.text = "★ %.1f".format(d.voteAverage ?: 0.0)
                val date = d.releaseDate ?: d.firstAirDate
                yearView.text = if (date.isNullOrEmpty()) "" else date.take(4)
                genresView.text = d.genres?.mapNotNull { it.name }?.joinToString(" • ") ?: ""
                overviewView.text = d.overview?.takeIf { it.isNotBlank() } ?: "Overview available nahi hai."

                playBtn.setOnClickListener {
                    val key = d.trailerKey()
                    if (key != null) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key)))
                    } else {
                        Toast.makeText(this@DetailActivity, "Iske liye trailer available nahi hai", Toast.LENGTH_SHORT).show()
                    }
                }
                moreBtn.setOnClickListener {
                    overviewView.visibility =
                        if (overviewView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
            }

            override fun onFailure(call: Call<Detail>, t: Throwable) {
                Toast.makeText(this@DetailActivity, "Network error: ${'$'}{t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
