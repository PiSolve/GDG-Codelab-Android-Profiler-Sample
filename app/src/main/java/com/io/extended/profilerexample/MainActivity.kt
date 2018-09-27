package com.io.extended.profilerexample

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Debug
import android.os.Handler
import android.os.PowerManager
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.sample_text


private const val TAG_PROFILER = "Profiler"
private const val TAG_WAKE_LOCK = "codeLab:evilLock"

class MainActivity : AppCompatActivity() {

    var millisecondTime: Long = 0
    var startTime: Long = 0
    var timeBuff: Long = 0
    var updateTime = 0L
    var seconds: Int = 0
    var minutes: Int = 0
    var milliseconds: Int = 0
    var handler: Handler = Handler()
    private var countDownTimer: CountDownTimer? = null
    private var runnable = object : Runnable {
        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime

            updateTime = timeBuff + millisecondTime

            seconds = (updateTime / 1000).toInt()

            minutes = seconds / 60

            seconds %= 60

            milliseconds = (updateTime % 1000).toInt()
            findViewById<TextView>(R.id.stop_watch_time).text = ("" + minutes + ":"
                    + String.format("%02d", seconds))
            /* + ":"
             + String.format("%03d", milliseconds))*/

            handler.postDelayed(this, 0)
        }

    }

    private lateinit var wl: PowerManager.WakeLock
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pm = getSystemService(
                Context.POWER_SERVICE) as PowerManager
        wl = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, TAG_WAKE_LOCK)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Example of a call to a native method
        setClickListeners(wl, runnable)
        setClickListenersFoTimer()

        checkPermissions()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        makeEverythingGone()
        startDownloadingImage()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        } else {
            if (item?.itemId == R.id.stopWatch) {
                changeToStopWatch()
            } else if (item?.itemId == R.id.timer) {
                changeToTimer()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setClickListeners(wl: PowerManager.WakeLock, runnable: Runnable) {
        findViewById<Button>(R.id.start_stop_watch).setOnClickListener {
            startStopWatch(wl, runnable)
        }
        findViewById<Button>(R.id.reset_stop_watch).setOnClickListener {
            resetStopWatch()
        }
        findViewById<Button>(R.id.stop_stop_watch).setOnClickListener {

            stopStopWatch(runnable, wl)
        }
    }

    private fun stopStopWatch(runnable: Runnable, wl: PowerManager.WakeLock) {
        timeBuff += millisecondTime

        handler.removeCallbacks(runnable)

        findViewById<Button>(R.id.reset_stop_watch).isEnabled = true
        wl.release()

        ///////////////////////////////////////////////////////////////////
        Debug.stopMethodTracing()
    }

    private fun resetStopWatch() {
        millisecondTime = 0L
        startTime = 0L
        timeBuff = 0L
        updateTime = 0L
        seconds = 0
        minutes = 0
        milliseconds = 0
        findViewById<TextView>(R.id.stop_watch_time).text = "00:00"

        // for cpu profiling to show case systrace

    }

    private fun startStopWatch(wl: PowerManager.WakeLock, runnable: Runnable) {
        // to show ..in energy profiler , about wake locks , acquire and release
        ////////////////////////////////////////////////////////////////////////////////////////////////
        Debug.startMethodTracing()
        wl.acquire()
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 0)

        findViewById<Button>(R.id.reset_stop_watch).isEnabled = false

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    0)

        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                Log.d(TAG_PROFILER, "Got new location $location")
            }
        }
    }

    private fun setClickListenersFoTimer() {
        findViewById<Button>(R.id.ten_timer).setOnClickListener { setTimer(10 * 1000L) }
        findViewById<Button>(R.id.twenty_timer).setOnClickListener { setTimer(20 * 1000L) }
        findViewById<Button>(R.id.thirty_timer).setOnClickListener { setTimer(30 * 1000L) }
    }

    private fun setTimer(i: Long) {
        countDownTimer?.onFinish()
        wl.acquire()
        countDownTimer?.cancel()
        cancelAlarm(this)
        setAlarm(this, i)
        countDownTimer = object : CountDownTimer(i, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                findViewById<TextView>(R.id.timer_text).text = "" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                Log.d(">>>Profiler", "onFinishCount Timer")
                if (wl.isHeld)
                    wl.release()
            }
        }
        countDownTimer?.start()

    }

    private fun changeToTimer() {
        resetStopWatch()
        findViewById<RelativeLayout>(R.id.stop_watch_layout).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.timer_layout).visibility = View.VISIBLE
        supportActionBar?.title = "Profiler"
        sample_text.text = "TIMER"
    }

    private fun changeToStopWatch() {
        countDownTimer?.onFinish()
        countDownTimer?.cancel()
        findViewById<TextView>(R.id.timer_text).text = "0"
        findViewById<RelativeLayout>(R.id.timer_layout).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.stop_watch_layout).visibility = View.VISIBLE
        supportActionBar?.title = "Profiler"
        sample_text.text = "STOP_WATCH"
    }

    private fun setAlarm(context: Context, timeDelay: Long) {
        Log.d(">>>Profiler", "alarmSet")
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val i = Intent(context, AlarmBroadCastReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeDelay, 0, pi) // Millisec * Second * Minute
    }

    private fun cancelAlarm(context: Context) {
        Log.d(">>>Profiler", "alarmCancelled")
        val intent = Intent(context, AlarmBroadCastReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    private fun makeEverythingGone() {
        findViewById<RelativeLayout>(R.id.timer_layout).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.stop_watch_layout).visibility = View.GONE
        sample_text.text = "Profiling"
    }


    private fun startDownloadingImage() {
        handler.postDelayed({ DownloadImageAsyncTask(this).execute(findViewById<LinearLayout>(R.id.activity_main)) }, 4000)

    }

}
