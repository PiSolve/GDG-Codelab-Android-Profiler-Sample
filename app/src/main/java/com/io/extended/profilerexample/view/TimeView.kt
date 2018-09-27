package com.io.extended.profilerexample.view

import android.content.Context
import android.graphics.Canvas
import android.os.SystemClock
import android.util.AttributeSet
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


private const val DEFAULT_TIME = "00:00.000"

class TimeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    private val startStopPairs = mutableListOf<Pair<Long, Long>>()
    private var latestStartTime = -1L

    override fun onDraw(canvas: Canvas?) {

        text = calculateCurrentTime()

        super.onDraw(canvas)
    }

    fun start() {
        if (latestStartTime < 0) {
            latestStartTime = timeNow()
        }
    }

    fun stop() {
        if (latestStartTime >= 0) {
            startStopPairs.add(latestStartTime to timeNow())
            latestStartTime = -1
        }
    }

    fun reset() {
        latestStartTime = -1
        startStopPairs.clear()
    }

    private fun calculateCurrentTime(): String {
        if (startStopPairs.isEmpty() && latestStartTime < 0) {
            return DEFAULT_TIME
        }

        var totalElapsedTime = 0L

        startStopPairs.forEach {
            totalElapsedTime += it.second - it.first
        }

        if (latestStartTime >= 0) {
            totalElapsedTime += timeNow() - latestStartTime
        }

        return formattedTime(totalElapsedTime)
    }

    private fun formattedTime(millis: Long): String {
        val date = Date(millis)
        val formatter = SimpleDateFormat("mm:ss.SSS", Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(date)
    }

    private fun timeNow() = SystemClock.uptimeMillis()

}
