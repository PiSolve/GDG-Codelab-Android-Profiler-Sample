package com.io.extended.profilerexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.io.extended.profilerexample.location.triggerLocationUpdates


var actionOnReceive = 1

private const val FIBONACCI_INDEX = 2000

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(">>>Profiler", "received alarm event ")
        when {
            actionOnReceive < 0 -> Log.d(">>>Profiler", "calculate fib is ${calculateFibonacci(FIBONACCI_INDEX)} ")
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

}
