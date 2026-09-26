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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Flexible Intent Key Detection
        val mediaId = intent.getLongExtra("MEDIA_ID", -1L).takeIf { it != -1L }
            ?: intent.getLongExtra("id", -1L)

        val mediaType = intent.getStringExtra("MEDIA_TYPE")
            ?: intent.getStringExtra("type")
            ?: "movie"

        if (mediaId == -1L) {
            Toast.makeText(this, "Media ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ScreenScape URL direct construction
        val targetUrl = if (mediaType == "tv") {
            "https://nxsha.screenscape.me/embed?tmdb=$mediaId&type=tv&s=1&e=1"
        } else {
            "https://nxsha.screenscape.me/embed?tmdb=$mediaId&type=movie"
        }

        // Force Button Text UI
        binding.btnPlayNow.text = "▶ Play Now"
        binding.btnPlayNow.setOnClickListener {
            openInAppPlayer(targetUrl)
        }

        fetchDetail(mediaType, mediaId)
    }

    private fun fetchDetail(type: String, id: Long) {
        lifecycleScope.launch {
            try {
                val detail = ApiClient.service.getDetail(type, id)
                binding.tvTitle.text = detail.title ?: detail.name ?: "Movie"
                binding.tvOverview.text = detail.overview ?: ""
                
                val rating = detail.voteAverage ?: 0.0
                val year = (detail.releaseDate ?: detail.firstAirDate ?: "").take(4)
                val runtimeStr = if (detail.runtime != null && detail.runtime > 0) "${detail.runtime} min" else ""

                binding.tvMeta.text = "$rating ★   $year   $runtimeStr"

                val backdropPath = detail.backdropPath ?: detail.posterPath
                if (!backdropPath.isNullOrEmpty()) {
                    Glide.with(this@DetailActivity)
                        .load("https://image.tmdb.org/t/p/w780$backdropPath")
                        .into(binding.ivBackdrop)
                }
            } catch (e: Exception) {
                // UI fail bhi ho tab bhi play ho jayega
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
