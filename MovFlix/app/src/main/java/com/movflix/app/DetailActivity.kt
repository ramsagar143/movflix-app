package com.movflix.app

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.movflix.app.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mediaId = intent.getLongExtra("id", -1L).takeIf { it != -1L } 
            ?: intent.getLongExtra("MEDIA_ID", -1L)
            
        val mediaType = intent.getStringExtra("type") 
            ?: intent.getStringExtra("MEDIA_TYPE") 
            ?: "movie"

        // Forcefully layout text overwrite
        binding.playBtn.text = "▶  Play Now"

        binding.playBtn.setOnClickListener {
            if (mediaId != -1L) {
                val streamUrl = if (mediaType == "movie") {
                    "https://nxsha.screenscape.me/embed?tmdb=$mediaId&type=movie"
                } else {
                    "https://nxsha.screenscape.me/embed?tmdb=$mediaId&type=tv&s=1&e=1"
                }
                openInAppPlayer(streamUrl)
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
