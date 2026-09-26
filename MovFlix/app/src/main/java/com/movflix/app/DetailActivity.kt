package com.movflix.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
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

        // Play Button Click Listener (ScreenScape Player Open karega)
        binding.playBtn.setOnClickListener {
            if (!streamUrl.isNullOrEmpty()) {
                openInAppPlayer(streamUrl!!)
            } else {
                Toast.makeText(this, "Streaming link available nahi hai", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDetail(type: String, id: Long) {
        lifecycleScope.launch {
            try {
                val detail = ApiClient.service.getDetail(type, id)
                streamUrl = detail.streamUrl

                binding.detailTitle.text = detail.title ?: detail.name ?: ""
                binding.detailOverview.text = detail.overview ?: ""
                binding.detailRating.text = "★ " + String.format("%.1f", detail.voteAverage ?: 0.0)

                val year = (detail.releaseDate ?: detail.firstAirDate ?: "").take(4)
                val runtimeStr = if (detail.runtime != null && detail.runtime > 0) "${detail.runtime} min" else ""
                binding.detailYear.text = "$year   $runtimeStr".trim()

                binding.detailGenres.text = detail.genres?.joinToString(" • ") { it.name } ?: ""

                Glide.with(this@DetailActivity)
                    .load("https://image.tmdb.org/t/p/w780" + detail.backdropPath)
                    .into(binding.detailBackdrop)

            } catch (e: Exception) {
                Toast.makeText(this@DetailActivity, "Data load karne me problem aayi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openInAppPlayer(url: String) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val webView = WebView(this)
        dialog.setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }
        }

        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(url)
        dialog.show()
    }
}
