package com.example.michael.newsarticlesapp

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.DocumentsContract
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.AppCompatImageView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.michael.newsarticlesapp.R.style.AppTheme
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_layout.*
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Array.get
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    val listArticle = ArrayList<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mSwipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)

        val urlArticles = "https://coinnews.jp/wp-json/wp/v2/posts/?_embed=''&page=1&per_page=20"

        AsyncTaskHandleJson().execute(urlArticles)

        Log.d("hogehoge", "test")
        //Log.d("hogehoge2", listArticle.get(0).url)

        //handleHtml(listArticle)
        handleHtmlTest(listArticle)

        articles_list.setOnItemClickListener { parent, view, position, id ->
            //intentにChrome Custom Tabsのデータを格納
            val intentChrome = CustomTabsIntent.Builder()
                    //Toolbarにタイトルを表示
                    .setShowTitle(true)
                    //Toolbarの色をMainActivityのToolbarと同じ色にする
                    .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    //MainActivityからWebViewに遷移するとき
                    .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
                    //WebViewからMainActivityに遷移するとき
                    .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    //戻るボタンを矢印に変更(未完)
                    .setCloseButtonIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_back))
                    .build()

            //Log.d("戻るボタン", CustomTabsIntent.Builder().setCloseButtonIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_arrow_back)).toString())

            //intentにURLを渡し、画面遷移
            intentChrome.launchUrl(this, Uri.parse(listArticle.get(position).url))
            Log.d("hogehoge3", listArticle.get(0).url)
        }

        /*
        mSwipeRefresh.setOnRefreshListener {
            AsyncTaskHandleJson().execute(url)
        }
        */


    }

    override fun onStart() {
        super.onStart()

    }




    inner class AsyncTaskHandleJson: AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {
            val text: String
            val connection = URL(url[0]).openConnection() as HttpURLConnection
            try {
                connection.connect()
                text = connection.inputStream.use { it.reader().use{reader -> reader.readText()} }
            }finally {
                connection.disconnect()
            }

            return text
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleJson(result)
        }
    }

    private fun handleJson(jsonString: String?) {

        val allJsonObject = JSONArray(jsonString)

        var i = 0
        while (i < allJsonObject.length()) {
            val jsonObject = allJsonObject.getJSONObject(i)
            val jsonObjectTitle = jsonObject.getJSONObject("title")

            val jsonObjectEmbedded = jsonObject.getJSONObject("_embedded")
            val jsonArrayMedia = jsonObjectEmbedded.getJSONArray("wp:featuredmedia")
            val jsonObjectMediaDetails0 = jsonArrayMedia.getJSONObject(0)
            val jsonObjectMediaDetails = jsonObjectMediaDetails0.getJSONObject("media_details")
            val jsonObjectMediaSizes = jsonObjectMediaDetails.getJSONObject("sizes")
            val jsonObjectImage = jsonObjectMediaSizes.getJSONObject("full")

            listArticle.add(Article(
                    jsonObjectTitle.getString("rendered"),
                    jsonObject.getString("date"),
                    jsonObject.getString("link"),
                    jsonObjectImage.getString("source_url")
            ))

            i++
        }

        val adapter = ListAdapte(this, listArticle)

        articles_list.adapter = adapter


    }
/*
    fun handleHtml(arrayList: ArrayList<Article>) {
        for (i in 0 .. 19) {
            val documentArticle = Jsoup.connect(arrayList.get(i).url).get()

            listArticleImage.add(ArticleImage(
                    documentArticle.getElementsByAttributeValue("property", "og:image").text()
            ))

        }

        val adapterImage = ListImageAdapte(this, listArticleImage)

        articles_list.adapter = adapterImage

    }

*/

    fun handleHtmlTest(list: ArrayList<Article>) {
            //val documentArticle = Jsoup.connect(list.get(0).url).get()


            //val articleImageTest = documentArticle.getElementsByAttributeValue("property", "og:image").text()

/*
            Picasso.get()
                    .load(articleImageTest)
                    .resize(2000,1500)
                    .centerCrop()
                    .into(articleImage)
*/


    }
/*

    inner class AsyncTaskHandleJsonArticle: AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            val textHtml: String
            val connection = URL(params[0]).openConnection() as HttpURLConnection
            try {
                connection.connect()
                textHtml = connection.inputStream.use { it.reader().use {reader -> reader.readText() } }

            } finally {
                connection.disconnect()
            }

            return textHtml
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleHtml(result)
        }

    }

    private fun handleHtml(htmlString: String?): String{
        val articleUrlImageView = findViewById<ImageView>(R.id.article_image_test)

        val document = Jsoup.parse(htmlString)
        val documentParse = document.getElementsByAttributeValue("property", "og:image")

        val textTest = documentParse.outerHtml().substring(35)

        val listArticleImage = textTest.substringBefore('"')

        return listArticleImage
    }
*/
}
