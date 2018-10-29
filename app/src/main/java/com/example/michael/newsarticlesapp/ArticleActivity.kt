package com.example.michael.newsarticlesapp

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.customtabs.CustomTabsIntent.*
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.util.Log.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

class ArticleActivity : AppCompatActivity() {
    val articleData = ArticleData("body", "url")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val textViewTitle =  findViewById<TextView>(R.id.text_position)
        val textViewDate = findViewById<TextView>(R.id.text_date)
        val textViewUrl = findViewById<TextView>(R.id.text_url)

        val extras = intent.extras ?: return
        val printTitle = extras.getString("article_title")
        val printDate = extras.getString("article_date")
        val printJsonUrl = extras.getString("article_json_url")

        textViewTitle.setText(printTitle.toString())
        textViewDate.setText(printDate.toString())


        AsyncTaskHandleJsonBody().execute(printJsonUrl)

        val filter = Linkify.TransformFilter { match, url -> articleData.articleUrl}
        //textViewに文字列をセット
        textViewUrl.setText("オリジナルサイトを読む ＞")
        //リンクにしたい文字列を指定
        val pattern = Pattern.compile("オリジナルサイトを読む ＞")
        //textViewにリンクを作成
        Linkify.addLinks(textViewUrl, pattern, articleData.articleUrl, null, filter)

        textViewUrl.setOnClickListener {
            //intentにChrome Custom Tabsのデータを格納
            val intentChrome = Builder()
                    //Toolbarにタイトルを表示
                    .setShowTitle(true)
                    //Toolbarの色をArticleActivityのToolbarと同じ色にする
                    .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    //ArticleActivityからWebViewに遷移するとき
                    .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
                    //WebViewからArticleActivityに遷移するとき
                    .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    //戻るボタンを矢印に変更(未完)
                    .setCloseButtonIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_back))
                    .build()

            //intentにURLを渡し、画面遷移
            intentChrome.launchUrl(this, Uri.parse(articleData.articleUrl))
        }


    }

    inner class AsyncTaskHandleJsonBody: AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            val body: String

            val con = URL(params[0]).openConnection() as HttpURLConnection

            try {
                con.connect()

                body =  con.inputStream.use { it.reader().use { reader -> reader.readText() } }

            }finally {
                con.disconnect()
            }
            return body

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val textViewBody = findViewById<TextView>(R.id.text_body)
            handleJsonArticle(result)

            textViewBody.setText(Html.fromHtml(articleData.articleBody))

        }

    }

    private fun handleJsonArticle(result: String?): ArticleData{
        val allJsonObject = JSONObject(result)

        //body
        val jsonObjectContent = allJsonObject.getJSONObject("content")
        articleData.articleBody = jsonObjectContent.getString("rendered")

        //link
        articleData.articleUrl = allJsonObject.getString("link")

        return articleData
    }
}
