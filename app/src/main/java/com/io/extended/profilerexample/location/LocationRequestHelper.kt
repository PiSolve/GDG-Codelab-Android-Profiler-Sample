/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.io.extended.profilerexample.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices


private var fusedLocationClient: FusedLocationProviderClient? = null

private const val UPDATE_INTERVAL = 2 * 1000L
private const val KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested"

internal object LocationRequestHelper {

    fun setRequesting(context: Context, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_LOCATION_UPDATES_REQUESTED, value)
                .apply()
    }

    fun getRequesting(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false)
    }
}

private fun locationProvider(context: Context) =
        fusedLocationClient
                ?: LocationServices.getFusedLocationProviderClient(context)
                        .apply { fusedLocationClient = this }

fun triggerLocationUpdates(context: Context) {

    val locationRequest = LocationRequest().apply {
        interval = UPDATE_INTERVAL
        fastestInterval = UPDATE_INTERVAL / 2
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //maxWaitTime = UPDATE_INTERVAL * 3
    }

    try {
        val task = locationProvider(context).requestLocationUpdates(locationRequest, getPendingIntent(context))
        Log.i(">>>Location", "Task result: ${task.result}")
    } catch (e: SecurityException) {
        Log.e(">>>>Location", "Missing permissions!", e)
    }
}

private fun getPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, LocationUpdatesBroadcastReceiver::class.java).apply {
        action = ACTION_PROCESS_UPDATES
    }
    return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun stopLocationUpdates(context: Context) {
    locationProvider(context).removeLocationUpdates(getPendingIntent(context))
}
