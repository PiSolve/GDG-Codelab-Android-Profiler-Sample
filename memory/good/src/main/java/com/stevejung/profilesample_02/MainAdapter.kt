package com.stevejung.profilesample_02

import android.graphics.BitmapFactory
import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat

class MainAdapter : RecyclerView.Adapter<ItemViewHolder>() {

    private val dataList = mutableListOf<Data>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    fun add(data:Data) {
        this.dataList.add(data)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dataList[position])
    }
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title by lazy { itemView.findViewById<TextView>(R.id.title) }
    private val date by lazy { itemView.findViewById<TextView>(R.id.date) }
    private val image by lazy { itemView.findViewById<ImageView>(R.id.image) }

    private val imageSize by lazy {itemView.context.resources.getDimensionPixelSize(R.dimen.image_width)}

    fun bind(data : Data) {
        title.text = data.title
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        date.text = simpleDateFormat.format(data.date)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(image.context.resources, data.imageRes, options)

        val scaleWidth = options.outWidth / imageSize
        val scaleHeight = options.outHeight / imageSize

        val finalScale = Math.max(scaleWidth, scaleHeight).let {
            if (it % 2 == 1) it +1 else it
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = finalScale

        image.setImageBitmap(BitmapFactory.decodeResource(image.context.resources, data.imageRes, options))
    }
}

data class Data(val title:String, val date : Long, @DrawableRes val imageRes:Int)