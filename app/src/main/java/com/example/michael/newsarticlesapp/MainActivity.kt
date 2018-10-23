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
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.michael.newsarticlesapp.R.style.AppTheme
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    val listArticle = ArrayList<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mSwipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)

        val url = "https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=150deaae17bd4a07968c8864bffc5e5a"

        AsyncTaskHandleJson().execute(url)



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

        }

        /*mSwipeRefresh.setOnRefreshListener {
            AsyncTaskHandleJson().execute()
        }*/

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

        val allJsonObject = JSONObject(jsonString)

        val articlesJsonArray = allJsonObject.getJSONArray("articles")

        var i = 0
        while (i < allJsonObject.getJSONArray("articles").length()) {
            val jsonObject = articlesJsonArray.getJSONObject(i)

            listArticle.add(Article(
                    jsonObject.getString("title"),
                    jsonObject.getString("publishedAt"),
                    jsonObject.getString("url"),
                    jsonObject.getString("urlToImage")
            ))

            i++
        }

        val adapter = ListAdapte(this, listArticle)

        articles_list.adapter = adapter


    }


}
