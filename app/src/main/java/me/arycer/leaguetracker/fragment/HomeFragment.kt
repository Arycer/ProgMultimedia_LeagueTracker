package me.arycer.leaguetracker.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.fragment.app.Fragment
import me.arycer.leaguetracker.R

class HomeFragment : Fragment(R.layout.fragment_home) {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<WebView>(R.id.youtubeWebView)
        webView.settings.javaScriptEnabled = true

        val videoId = "I76wvt0aEE4"

        val videoHtml = """
            <html>
                <body style="margin:0;padding:0;">
                    <iframe width="100%" height="100%" src="https://www.youtube.com/embed/$videoId" frameborder="0" allowfullscreen></iframe>
                </body>
            </html>
        """.trimIndent()

        webView.loadData(videoHtml, "text/html", "utf-8")
    }
}