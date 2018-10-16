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
import android.os.PowerManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.io.extended.profilerexample.location.LocationRequestHelper
import com.io.extended.profilerexample.location.stopLocationUpdates
import com.io.extended.profilerexample.location.triggerLocationUpdates
import com.io.extended.profilerexample.view.TimeView
import com.stevejung.profilesample.CpuActivity
import com.stevejung.profilesample_00_memory.MemoryActivity
import kotlinx.android.synthetic.main.activity_main.sample_text


private const val TAG_PROFILER = "Profiler"
private const val TAG_WAKE_LOCK = "codeLab:evilLock"

class MainActivity : AppCompatActivity() {

    private var countDownTimer: CountDownTimer? = null
    private var imageCount = 0
    private val downloadUrls = listOf(
            "http://mobw.org/wp-content/uploads/2017/04/Beautiful-Night-Sky-Wallpapers.jpg",
            "http://knowbystillmotion.com/wp-content/uploads/2018/07/World-Map-Black-And-White-Tumblr-Best-Of-Black-Marble-As-World-Map-Black-And-White-Tumblr-Best-Of-Black-Marble-Just-Let-It-Be-Quote-Grunge-Tumblr-Aesthetic-Iphone-1-6.jpg",
            "http://getwallpapers.com/wallpaper/full/8/5/d/454230.jpg",
            "http://mobw.org/wp-content/uploads/2017/05/Black-Night-Sky-Wallpaper-for-Mobile.jpg",
            "http://spliffmobile.com/download/dark-night-6698.jpg",
            "http://getwallpapers.com/wallpaper/full/f/e/e/328184.jpg",
            "http://genchi.info/image/wallpaper-night-2.jpg")

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
        setClickListeners(wl)
        setClickListenersFoTimer()

        checkPermissions()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        makeEverythingGone()
        findViewById<Switch>(R.id.location_switch).setOnCheckedChangeListener { _, isChecked ->
            toggleLocationUpdates(isChecked)
        }
        findViewById<Button>(R.id.download_image).setOnClickListener {
            downloadNewBackground()
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<Switch>(R.id.location_switch).isChecked = LocationRequestHelper.getRequesting(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            makeEverythingGone()
        } else if (item != null){
            when (item.itemId) {
                R.id.stopWatch -> changeToStopWatch()
                R.id.timer -> changeToTimer()
                R.id.cpu -> openCpu()
                R.id.memory -> openMemory()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openMemory() {
        startActivity(Intent(this, MemoryActivity::class.java))
    }

    private fun openCpu() {
        startActivity(Intent(this, CpuActivity::class.java))
    }

    private fun setClickListeners(wl: PowerManager.WakeLock) {
        findViewById<Button>(R.id.start_stop_watch).setOnClickListener {
            startStopWatch(wl)
        }
        findViewById<Button>(R.id.reset_stop_watch).setOnClickListener {
            resetStopWatch()
        }
        findViewById<Button>(R.id.stop_stop_watch).setOnClickListener {
            stopStopWatch(wl)
        }
    }

    private fun stopStopWatch(wl: PowerManager.WakeLock) {
        findViewById<TimeView>(R.id.stop_watch_time).stop()

        findViewById<Button>(R.id.reset_stop_watch).isEnabled = true
        wl.release()

        ///////////////////////////////////////////////////////////////////
        Debug.stopMethodTracing()
    }

    private fun resetStopWatch() {
        findViewById<TimeView>(R.id.stop_watch_time).reset()
    }

    private fun startStopWatch(wl: PowerManager.WakeLock) {
        // to show ..in energy profiler , about wake locks , acquire and release
        ////////////////////////////////////////////////////////////////////////////////////////////////
        Debug.startMethodTracing()
        wl.acquire()

        findViewById<TimeView>(R.id.stop_watch_time).start()

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
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    ),
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
                findViewById<TextView>(R.id.timer_text).text = "${millisUntilFinished / 1000}"
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
        findViewById<ViewGroup>(R.id.home_layout).visibility = View.GONE
        findViewById<ViewGroup>(R.id.stop_watch_layout).visibility = View.GONE
        findViewById<ViewGroup>(R.id.timer_layout).visibility = View.VISIBLE
        supportActionBar?.title = "Profiler"
        sample_text.text = "TIMER"
    }

    private fun changeToStopWatch() {
        countDownTimer?.onFinish()
        countDownTimer?.cancel()
        findViewById<TextView>(R.id.timer_text).text = "0"
        findViewById<ViewGroup>(R.id.home_layout).visibility = View.GONE
        findViewById<ViewGroup>(R.id.timer_layout).visibility = View.GONE
        findViewById<ViewGroup>(R.id.stop_watch_layout).visibility = View.VISIBLE
        supportActionBar?.title = "Profiler"
        sample_text.text = "STOP_WATCH"
    }

    private fun setAlarm(context: Context, timeDelay: Long) {
        Log.d(">>>Profiler", "alarmSet")
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + timeDelay,
                0,
                pendingIntentForAlarm(context))
    }

    private fun cancelAlarm(context: Context) {
        Log.d(">>>Profiler", "alarmCancelled")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntentForAlarm(context))
    }

    private fun pendingIntentForAlarm(context: Context): PendingIntent {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    private fun makeEverythingGone() {
        findViewById<ViewGroup>(R.id.timer_layout).visibility = View.GONE
        findViewById<ViewGroup>(R.id.stop_watch_layout).visibility = View.GONE
        findViewById<ViewGroup>(R.id.home_layout).visibility = View.VISIBLE
        sample_text.text = "Profiling"
    }

    private fun downloadNewBackground() {
        val url = downloadUrls[imageCount % downloadUrls.size]
        imageCount++
        DownloadImageAsyncTask(this, url)
                .execute(findViewById<LinearLayout>(R.id.activity_main))
    }

    private fun toggleLocationUpdates(enabled: Boolean) {
        val ctx = this.applicationContext
        LocationRequestHelper.setRequesting(ctx, enabled)
        if (enabled) {
            triggerLocationUpdates(ctx)
        } else {
            stopLocationUpdates(ctx)
        }
    }

}
