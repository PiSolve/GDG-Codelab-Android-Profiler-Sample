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


const val IMAGE_URL = "https://www.beautycolorcode.com/38e6d3.png"

class DownloadImageAsyncTask(private val context: Context) : AsyncTask<View, Void, Bitmap>() {

    var imgView: View? = null

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to [.execute]
     * by the caller of this task.
     *
     * This method can call [.publishProgress] to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     *
     * @return A result, defined by the subclass of this task.
     *
     * @see .onPreExecute
     * @see .onPostExecute
     *
     * @see .publishProgress
     */
    override fun doInBackground(vararg params: View?): Bitmap? {
        Log.d(">>>Profiler", "startDownloading")
        imgView = params[0]
        return download_Image()

    }

    override fun onPostExecute(result: Bitmap?) {
        Log.d(">>>Profiler", "downloadingDone")
        result?.run {
            var ob = BitmapDrawable(context.resources, result)
            imgView?.background = ob
        }
        super.onPostExecute(result)
    }

    private fun download_Image(): Bitmap? {

        var bmp: Bitmap? = null
        try {
            val ulrn = URL(IMAGE_URL)
            val con = ulrn.openConnection() as HttpURLConnection
            val stream = con.inputStream
            bmp = BitmapFactory.decodeStream(stream)
            if (null != bmp)
                return bmp

        } catch (e: Exception) {
            Log.d(">>>Profiler", "exception $e")
            e.printStackTrace()
        }

        return bmp
    }
}
