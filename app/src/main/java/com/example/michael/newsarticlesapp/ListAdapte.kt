package com.example.michael.newsarticlesapp

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.squareup.picasso.Picasso

class ListAdapte(val context: Context, val list: ArrayList<Article>/*, val onArticleClicked: (Article) -> Unit*/): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.row_layout,parent, false)

        val articleImage = view.findViewById(R.id.article_image) as AppCompatImageView
        val articleTitle = view.findViewById(R.id.article_title) as AppCompatTextView
        val articleDate = view.findViewById(R.id.article_date) as AppCompatTextView

        Picasso.get()
                .load(list[position].urlToImage)
                .fit()
                .into(articleImage)


        articleTitle.text = list[position].title
        articleDate.text = list[position].date

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}