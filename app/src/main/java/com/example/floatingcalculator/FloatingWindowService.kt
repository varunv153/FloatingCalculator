package com.example.floatingcalculator

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.app.NotificationCompat
import kotlin.math.max

class FloatingWindowService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0.0f
    private var initialTouchY: Float = 0.0f
    private var isResizing: Boolean = false
    private val minWindowSize: Int = 50
    private var initialWidth: Int = 0
    private var initialHeight: Int = 0


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        initializeFloatingWindow()
        mFloatingView!!.findViewById<EditText>(R.id.editText)?.setOnTouchListener { _, event -> handleFloatingViewTouch(event) }
        mFloatingView!!.setOnTouchListener { _, event -> handleFloatingViewTouch(event) }
        mFloatingView!!.findViewById<Button>(R.id.resize_handle)?.setOnTouchListener { _, event -> handleResizeHandleTouch(event) }

        startForeground(1, createDummyNotification())

    }

    private fun createDummyNotification(): Notification {

        val channelId = "YourChannelId"
        // Create a notification channel (for Android 8.0 and higher)
        val channel = NotificationChannel(
            channelId,
            "Your Notification Channel Name",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build and return the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Your Notification Title")
            .setContentText("Your Notification Text")
            .setContentIntent(pendingIntent)

        return builder.build()
    }


    fun onNumberClick(view: View?) {
        val calculatorViewManager = CalculatorViewManager()
        if (view is Button) {
            calculatorViewManager.handleClickInCalculator(mFloatingView!!, view)
        }
    }

    private fun initializeFloatingWindow() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null)
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params: WindowManager.LayoutParams = WindowManager.LayoutParams(
            Resources.getSystem().displayMetrics.widthPixels/2,
            Resources.getSystem().displayMetrics.heightPixels/3,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER or Gravity.START

        mWindowManager!!.addView(mFloatingView, params)
    }

    private fun handleFloatingViewTouch(event: MotionEvent): Boolean {
        val params: WindowManager.LayoutParams = mFloatingView!!.layoutParams as WindowManager.LayoutParams
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Record the initial touch and view position
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isResizing) {
                    // Not resizing, so it's dragging
                    params.x = (initialX + (event.rawX - initialTouchX)).toInt()
                    params.y = (initialY + (event.rawY - initialTouchY)).toInt()
                    mWindowManager?.updateViewLayout(mFloatingView, params)
                    return true
                }
                return false
            }
            MotionEvent.ACTION_UP -> {
                isResizing = false
                return true
            }
            else -> return false
        }
    }

    private fun handleResizeHandleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Record the initial touch and view size
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                initialWidth = mFloatingView!!.width
                initialHeight = mFloatingView!!.height
                isResizing = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isResizing) {
                    val deltaX = event.rawX - initialTouchX
                    val deltaY = event.rawY - initialTouchY
                    val newWidth = max(initialWidth + deltaX.toInt(), minWindowSize)
                    val newHeight = max(initialHeight + deltaY.toInt(), minWindowSize)

                    // Update the view size
                    mFloatingView!!.layoutParams?.width = newWidth
                    mFloatingView!!.layoutParams?.height = newHeight
                    mWindowManager!!.updateViewLayout(mFloatingView, mFloatingView!!.layoutParams)
                    return true
                }
                return false
            }
            MotionEvent.ACTION_UP -> {
                isResizing = false
                return true
            }
            else -> return false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) {
            mWindowManager!!.removeView(mFloatingView)
        }
    }
}
