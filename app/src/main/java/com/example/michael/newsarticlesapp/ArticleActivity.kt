package com.example.michael.newsarticlesapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ArticleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        val textViewPosition =  findViewById<TextView>(R.id.text_position)
        val textViewUrl = findViewById<TextView>(R.id.text_url)

        val extras = intent.extras ?: return
        val print_position = extras.getInt("position_ex")
        val print_url = extras.getString("url_article")

        textViewPosition.setText(print_position.toString())
        textViewUrl.setText(print_url)
    }
}
