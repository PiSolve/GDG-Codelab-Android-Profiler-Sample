package com.io.extended.profilerexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_emotional_layout.*


class EmotionalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotional_layout)
        findViewById<EmotionalView>(R.id.happyButton).setOnClickListener {
            findViewById<EmotionalView>(R.id.emotionalFaceView).happinessState = EmotionalView.HAPPY
        }

        findViewById<EmotionalView>(R.id.sadButton).setOnClickListener {
            findViewById<EmotionalView>(R.id.emotionalFaceView).happinessState = EmotionalView.SAD
        }

    }

}
