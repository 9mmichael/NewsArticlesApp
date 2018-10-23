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
import android.util.Log
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

//        val articleImage = view.findViewById(R.id.article_image) as AppCompatImageView
        val articleTitle = view.findViewById(R.id.article_title) as AppCompatTextView
        val articleDate = view.findViewById(R.id.article_date) as AppCompatTextView

/*        Picasso.get()
                .load(list[position].urlToImage)
                .resize(2000,1500)
                .centerCrop()
                .into(articleImage)
*/

        if(list[position].title.length > 50) {
            articleTitle.text = list[position].title.substring(0, 50) + "..."
        }
        else {
            articleTitle.text = list[position].title
        }

        val dateFormatBefore = "yyyy-MM-dd'T'HH:mm:ss"
        val dateFormatAfter = "MM'/'dd'('E')' HH':'mm"

        //String型のフォーマットをSimpleDateFormat型に変換
        val sdfBefore = SimpleDateFormat(dateFormatBefore)
        val sdfAfter = SimpleDateFormat(dateFormatAfter)

        sdfBefore.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))
        //変換したフォーマットにJSTで取得するという情報を与える
        sdfAfter.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))

        //String型の日時のデータをパースしてDate型に変換
        val dateBefore = sdfBefore.parse(list[position].date)

        articleDate.text = sdfAfter.format(dateBefore).toString()
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