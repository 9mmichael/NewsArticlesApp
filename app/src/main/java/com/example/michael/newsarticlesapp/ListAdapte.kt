package com.example.michael.newsarticlesapp

import android.content.Context
import android.icu.text.DateTimePatternGenerator
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.icu.util.TimeZone
import android.widget.Toast
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.util.*
import java.text.ParseException
import java.time.format.DateTimeFormatter

class ListAdapte(val context: Context, val list: ArrayList<Article>/*, val onArticleClicked: (Article) -> Unit*/): BaseAdapter() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.row_layout,parent, false)

        val articleImage = view.findViewById(R.id.article_image) as AppCompatImageView
        val articleTitle = view.findViewById(R.id.article_title) as AppCompatTextView
        val articleDate = view.findViewById(R.id.article_date) as AppCompatTextView

        Picasso.get()
                .load(list[position].urlToImage)
                .resize(2000,1500)
                .centerCrop()
                .into(articleImage)


        articleTitle.text = list[position].title

        val dateFormatUtc = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        val dateFormatJst = "MM'/'dd'('E')' HH':'mm"

        //String型のフォーマットをSimpleDateFormat型に変換
        val sdfUtc = SimpleDateFormat(dateFormatUtc)
        val sdfJst = SimpleDateFormat(dateFormatJst)

        //変換したフォーマットにUTCで取得するという情報を与える
        sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"))
        //変換したフォーマットにJSTで取得するという情報を与える
        sdfJst.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))

        //String型の日時のデータをパースしてDate型に変換
        val dateUtc = sdfUtc.parse(list[position].date)

        articleDate.text = sdfJst.format(dateUtc).toString()
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