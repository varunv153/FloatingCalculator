package com.example.floatingcalculator;

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.example.floatingcalculator.R

class FloatingWindowService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Inflate the layout for the floating window
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null)

        // Set up the WindowManager
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER or Gravity.START
        params.x = 0
        params.y = 0

        mWindowManager?.addView(mFloatingView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) {
            mWindowManager?.removeView(mFloatingView)
        }
    }
}
