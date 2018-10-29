package com.example.michael.newsarticlesapp

data class ListArticle(
        val title: String,
        val date: String,
        val jsonUrl: String,
        var urlToImage: String
)