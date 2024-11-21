package com.example.practice20kotline

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroscope: Sensor? = null

    private lateinit var textGyroscopeData: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonStop: Button

    private var isThreadRunning = false
    private var updateThread: Thread? = null

    private var gyroscopeData: FloatArray = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        textGyroscopeData = findViewById(R.id.text_gyroscope_data)
        buttonStart = findViewById(R.id.button_start)
        buttonStop = findViewById(R.id.button_stop)


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)


        buttonStart.setOnClickListener {
            if (!isThreadRunning) {
                startGyroscopeThread()
            }
        }


        buttonStop.setOnClickListener {
            stopGyroscopeThread()
        }
    }

    private fun startGyroscopeThread() {
        isThreadRunning = true


        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI)


        updateThread = Thread {
            while (isThreadRunning) {
                runOnUiThread {
                    textGyroscopeData.text = """
                        Ось X: ${gyroscopeData[0]}
                        Ось Y: ${gyroscopeData[1]}
                        Ось Z: ${gyroscopeData[2]}
                    """.trimIndent()
                }
                Thread.sleep(500)
            }
        }
        updateThread?.start()
    }

    private fun stopGyroscopeThread() {
        isThreadRunning = false
        updateThread?.interrupt()
        updateThread = null


        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            gyroscopeData[0] = event.values[0]
            gyroscopeData[1] = event.values[1]
            gyroscopeData[2] = event.values[2]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        stopGyroscopeThread()
    }
}