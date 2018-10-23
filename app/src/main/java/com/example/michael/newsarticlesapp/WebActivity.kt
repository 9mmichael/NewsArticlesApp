package com.example.michael.newsarticlesapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.WorkSource
import android.webkit.WebView
import android.webkit.WebViewClient

class WebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val webViewArticle = findViewById<WebView>(R.id.web_view)

        //intentのextrasに何も入っていなかったらそのままreturn
        val extras = intent.extras ?: return


        var loadArticleUrl = extras.getString("article_url")

        //リンクをクリックした時に標準ブラウザに遷移せず、WebView内に表示する
        webViewArticle.setWebViewClient(WebViewClient())

        //WebViewで表示したいサイトのURLをloadUrlメソッドで渡す
        webViewArticle.loadUrl(loadArticleUrl)

        //test2
    }
}
