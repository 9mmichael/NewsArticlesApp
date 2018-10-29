package com.example.michael.newsarticlesapp

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
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
import android.widget.*
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
import java.util.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    val listArticle = ArrayList<ListArticle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mSwipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)

        val urlArticles = "https://coinnews.jp/wp-json/wp/v2/posts/?_embed=''&page=1&per_page=20"

        AsyncTaskHandleJson().execute(urlArticles)

        AsyncTaskHandleJsonHeader().execute(urlArticles)

        articles_list.setOnItemClickListener { parent, view, position, id ->

            val intentWebView = Intent(this, ArticleActivity::class.java)

            intentWebView.putExtra("article_title", listArticle.get(position).title)
            intentWebView.putExtra("article_date", listArticle.get(position).date)
            intentWebView.putExtra("article_url", listArticle.get(position).jsonUrl)
            intentWebView.putExtra("article_json_url", listArticle.get(position).jsonUrl)

            startActivity(intentWebView)
        }


        /*
        mSwipeRefresh.setOnRefreshListener {
            AsyncTaskHandleJson().execute(url)
        }
        */


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

            //title
            val jsonObjectTitle = jsonObject.getJSONObject("title")

            //date
            val articleDateJst = utcToJst(jsonObject.getString("date"))

            //jsonUrl
            val jsonObjectLinks = jsonObject.getJSONObject("_links")
            val jsonArraySelf = jsonObjectLinks.getJSONArray("self")
            val jsonObjectSelf0 = jsonArraySelf.getJSONObject(0)

            //urlToImage
            val jsonObjectEmbedded = jsonObject.getJSONObject("_embedded")
            val jsonArrayMedia = jsonObjectEmbedded.getJSONArray("wp:featuredmedia")
            val jsonObjectMediaDetails0 = jsonArrayMedia.getJSONObject(0)
            val jsonObjectMediaDetails = jsonObjectMediaDetails0.getJSONObject("media_details")
            val jsonObjectMediaSizes = jsonObjectMediaDetails.getJSONObject("sizes")
            val jsonObjectImage = jsonObjectMediaSizes.getJSONObject("full")

            listArticle.add(ListArticle(
                    jsonObjectTitle.getString("rendered"),
                    articleDateJst,
                    jsonObjectSelf0.getString("href"),
                    jsonObjectImage.getString("source_url")
            ))

            i++
        }

        val adapter = ListAdapte(this, listArticle)

        articles_list.adapter = adapter


    }

    inner class AsyncTaskHandleJsonHeader: AsyncTask<String, String, String?>() {

        //別スレッドで実行
        override fun doInBackground(vararg params: String?): String? {
            val infoTotalPages :String?
            val connection = URL(params[0]).openConnection() as HttpURLConnection

            try {

                //通信開始
                connection.connect()

                //Headerから総ページ数を取得
                infoTotalPages = connection.getHeaderField("X-WP-TotalPages")
            } finally {

                //通信切断
                connection.disconnect()
            }

            //onPostExecute()に渡す
            return infoTotalPages
        }

        override fun onPostExecute(result: String?) {

            //doInBackgroundの戻り値をresultに格納
            super.onPostExecute(result)
/*
            //MainActivityのxmlで作成したTextViewへの参照
            val textViewTotalPages = findViewById<TextView>(R.id.total_pages)

            //参照したTextViewにdoInBackground()から受け取った総ページ数を格納
            textViewTotalPages.text = result
*/
        }
    }

    fun utcToJst(utc: String): String{
        val dateFormatBefore = "yyyy-MM-dd'T'HH:mm:ss"
        val dateFormatAfter = "MM'/'dd'('E')' HH':'mm"

        //String型のフォーマットをSimpleDateFormat型に変換
        val sdfBefore = SimpleDateFormat(dateFormatBefore)
        val sdfAfter = SimpleDateFormat(dateFormatAfter)

        sdfBefore.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))
        //変換したフォーマットにJSTで取得するという情報を与える
        sdfAfter.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))

        //String型の日時のデータをパースしてDate型に変換
        val dateBefore = sdfBefore.parse(utc)

        return sdfAfter.format(dateBefore).toString()
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
