package com.io.extended.profilerexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.io.extended.profilerexample.location.triggerLocationUpdates


var actionOnReceive = -1

private const val FIBONACCI_INDEX = 42

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(">>>Profiler", "received alarm event ")
        when {
            actionOnReceive < 0 -> {
                Log.d(">>>Profiler", "calculate fib is ${calculateFibonacci(FIBONACCI_INDEX)} ")
                Log.d(">>>Native Profiler", "calculate fib is ${nativeFibonacci(FIBONACCI_INDEX)} ")
                Log.d(">>>Native Fast Profiler", "calculate fib is ${nativeFastFibonacci(FIBONACCI_INDEX)} ")
            }
            actionOnReceive > 0 -> SampleApp.getInstance()?.let { triggerLocationUpdates(it) }
            actionOnReceive == 0 -> {
                Log.d(">>>Profiler", "calculate fib is ${calculateFibonacci(FIBONACCI_INDEX)} ")
                context?.let { triggerLocationUpdates(it) }
            }
        }
    }

    private fun calculateFibonacci(num: Int): Int {
        if (num <= 0) return 0
        if (num == 1) return 1

        return calculateFibonacci(num - 1) + calculateFibonacci(num - 2)
    }



    external fun nativeFibonacci(number: Int): Int
    external fun nativeFastFibonacci(number: Int): Int

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
