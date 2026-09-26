package com.movflix.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.movflix.app.databinding.ActivityDetailBinding
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var streamUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mediaId = intent.getLongExtra("MEDIA_ID", -1)
        val mediaType = intent.getStringExtra("MEDIA_TYPE") ?: "movie"

        if (mediaId == -1L) {
            finish()
            return
        }

        fetchDetail(mediaType, mediaId)

        binding.btnPlayTrailer.text = "Play Now"
        binding.btnPlayTrailer.setOnClickListener {
            if (!streamUrl.isNullEmpty()) {
                openInAppPlayer(streamUrl!!)
            } else {
                Toast.makeText(this, "Streaming link not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDetail(type: String, id: Long) {
        lifecycleScope.launch {
            try {
                val detail = ApiClient.service.getDetail(type, id)
                streamUrl = detail.streamUrl

                binding.tvTitle.text = detail.title ?: detail.name ?: ""
                binding.tvOverview.text = detail.overview ?: ""
                binding.tvRating.text = String.format("%.1f", detail.voteAverage ?: 0.0)

                val year = (detail.releaseDate ?: detail.firstAirDate ?: "").take(4)
                val runtimeStr = if (detail.runtime != null && detail.runtime > 0) "${detail.runtime} min" else ""
                binding.tvMeta.text = "$year  $runtimeStr".trim()

                binding.tvGenres.text = detail.genres?.joinToString(" • ") { it.name } ?: ""

                Glide.with(this@DetailActivity)
                    .load("https://image.tmdb.org/t/p/w780" + detail.backdropPath)
                    .into(binding.ivBackdrop)

            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Failed to load content", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openInAppPlayer(url: String) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val webView = WebView(this)
        dialog.setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        webView.loadUrl(url)
        dialog.show()
    }
}
