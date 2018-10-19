package com.example.michael.newsarticlesapp

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.util.Log
import android.webkit.WebView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
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

        val url = "https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=150deaae17bd4a07968c8864bffc5e5a"

        AsyncTaskHandleJson().execute(url)



        articles_list.setOnItemClickListener { parent, view, position, id ->

            //アプリ内部でWebViewを開く
            //遷移元と遷移先のActivityがどこかをIntentに渡す
            val intentWebView = Intent(this@MainActivity, WebActivity::class.java)

            //タップされた記事のURLをextraに渡す
            intentWebView.putExtra("article_url", listArticle.get(position).url)

            //Intentに設定されたActivityに遷移
            startActivity(intentWebView, ActivityOptions.makeSceneTransitionAnimation(this@MainActivity).toBundle())

        }



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
