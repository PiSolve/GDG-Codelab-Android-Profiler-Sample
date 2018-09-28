package com.io.extended.profilerexample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.util.Log
import android.view.View
import java.net.HttpURLConnection
import java.net.URL


class DownloadImageAsyncTask(
        private val context: Context,
        private val url:String
) : AsyncTask<View, Void, Bitmap>() {

    private var imgView: View? = null

    override fun doInBackground(vararg params: View?): Bitmap? {
        Log.d(">>>Profiler", "startDownloading")
        imgView = params[0]
        return downloadImage()
    }

    override fun onPostExecute(result: Bitmap?) {
        Log.d(">>>Profiler", "downloadingDone")
        result?.run {
            val ob = BitmapDrawable(context.resources, result)
            imgView?.background = ob
            imgView = null
        }
        super.onPostExecute(result)
    }

    private fun downloadImage(): Bitmap? {

        var bmp: Bitmap? = null
        try {
            val iUrl = URL(url)
            val con = iUrl.openConnection() as HttpURLConnection
            val stream = con.inputStream
            bmp = BitmapFactory.decodeStream(stream)
            con.disconnect()

            if (null != bmp) return bmp
        } catch (e: Exception) {
            Log.e(">>>Profiler", "Error downloading background image!", e)
        }

        return bmp
    }
}
