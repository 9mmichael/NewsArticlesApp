package com.example.michael.newsarticlesapp

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class ListAdapte(val context: Context, val list: ArrayList<Article>): BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.row_layout,parent, false)

        val articleTitle = view.findViewById(R.id.article_title) as AppCompatTextView
        val articleDate = view.findViewById(R.id.article_date) as AppCompatTextView

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