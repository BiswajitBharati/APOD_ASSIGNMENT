package com.goldmansachs.apod.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.goldmansachs.apod.R
import com.goldmansachs.apod.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.name
    }

    private lateinit var binding: ActivityMediaBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Video Url: https://www.youtube.com/embed/c4Xky6tlFyY?rel=0&VQ=HD720
        val mediaType = intent.getStringExtra("Media_Type")
        val mediaUrl = intent.getStringExtra("Media_Url")
        Log.d(TAG, "onCreate() == mediaType: $mediaType, mediaUrl: $mediaUrl")

        mediaUrl?.let {
            if (mediaType == "video") {
                binding.imageLoader.visibility = View.GONE
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.VISIBLE
                binding.webView.settings.javaScriptEnabled = true
                binding.webView.loadUrl(it)
                binding.webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val url = request?.url.toString()
                        view?.loadUrl(url)
                        return super.shouldOverrideUrlLoading(view, request)
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        binding.imageLoader.visibility = View.VISIBLE
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        binding.imageLoader.visibility = View.GONE
                        super.onPageFinished(view, url)
                    }

                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        binding.imageLoader.visibility = View.GONE
                        super.onReceivedError(view, request, error)
                    }
                }
            } else {
                binding.imageLoader.visibility = View.VISIBLE
                binding.imageView.visibility = View.VISIBLE
                binding.webView.visibility = View.GONE

                Glide.with(this)
                    .load(mediaUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            binding.imageLoader.visibility = View.GONE
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.imageLoader.visibility = View.GONE
                            return false
                        }
                    })
                    .placeholder(R.drawable.nasa)
                    .into(binding.imageView)
            }
        } ?: run {
            finish()
        }
    }
}