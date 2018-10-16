package com.example.michael.newsarticlesapp

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val url = "https://newsapi.org/v2/top-headlines?sources=bbc-news&apiKey=150deaae17bd4a07968c8864bffc5e5a"

        AsyncTaskHandleJson().execute(url)


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

        val jsonArray = JSONObject(jsonString)

        val articlesJsonArray = jsonArray.getJSONArray("articles")


        val list = ArrayList<Article>()

        var i = 0
        while (i < jsonArray.length()) {
            val jsonObject = articlesJsonArray.getJSONObject(i)

            list.add(Article(
                    jsonObject.getString("title"),
                    jsonObject.getString("url"),
                    jsonObject.getString("urlToImage"),
                    jsonObject.getString("publishedAt")
            ))

            i++
        }

        val adapter = ListAdapte(this, list)

        articles_list.adapter = adapter

    }

}
