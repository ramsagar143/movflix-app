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

        val mediaId = intent.getLongExtra("MEDIA_ID", -1L).takeIf { it != -1L }
            ?: intent.getLongExtra("id", -1L)

        val mediaType = intent.getStringExtra("MEDIA_TYPE")
            ?: intent.getStringExtra("type")
            ?: "movie"

        if (mediaId == -1L) {
            Toast.makeText(this, "Invalid content", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Direct ScreenScape URL construct karke Fallback setup kar do
        streamUrl = if (mediaType == "movie") {
            "https://nxsha.screenscape.me/embed?tmdb=$mediaId&type=movie"
        } else {
            "https://nxsha.screenscape.me/embed?tmdb=$mediaId&type=tv&s=1&e=1"
        }

        binding.btnPlayTrailer.text = "▶ Play Now"
        binding.btnPlayTrailer.setOnClickListener {
            streamUrl?.let { url ->
                openInAppPlayer(url)
            } ?: run {
                Toast.makeText(this, "Streaming URL not found", Toast.LENGTH_SHORT).show()
            }
        }

        fetchDetail(mediaType, mediaId)
    }

    private fun fetchDetail(type: String, id: Long) {
        lifecycleScope.launch {
            try {
                val detail = ApiClient.service.getDetail(type, id)
                
                // Backend URL override if available
                if (!detail.streamUrl.isNullOrEmpty()) {
                    streamUrl = detail.streamUrl
                }

                binding.tvTitle.text = detail.title ?: detail.name ?: "Unknown"
                binding.tvOverview.text = detail.overview ?: "No description available."

                val rating = detail.voteAverage ?: 0.0
                val year = (detail.releaseDate ?: detail.firstAirDate ?: "").take(4)
                val runtimeStr = if (detail.runtime != null && detail.runtime > 0) "${detail.runtime} min" else ""

                binding.tvMeta.text = listOf(
                    if (rating > 0) "★ ${String.format("%.1f", rating)}" else null,
                    year.ifEmpty { null },
                    runtimeStr.ifEmpty { null }
                ).filterNotNull().joinToString("   ")

                binding.tvGenres.text = detail.genres?.joinToString(" • ") { it.name } ?: ""

                val backdropPath = detail.backdropPath ?: detail.posterPath
                if (!backdropPath.isNullOrEmpty()) {
                    Glide.with(this@DetailActivity)
                        .load("https://image.tmdb.org/t/p/w780$backdropPath")
                        .into(binding.ivBackdrop)
                }

            } catch (e: Exception) {
                // Keep default calculated ScreenScape URL even if detail API fails
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
            useWideViewPort = true
            loadWithOverviewMode = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                return false
            }
        }

        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(url)
        dialog.show()
    }
}
