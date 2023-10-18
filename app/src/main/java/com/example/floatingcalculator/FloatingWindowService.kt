package com.example.floatingcalculator

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
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

        // Inflate the layout for the floating window
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null)

        // Find the resize handle view by its ID
        val resizeHandle: Button? = mFloatingView?.findViewById(R.id.resize_handle)

        // Set up the WindowManager
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params: WindowManager.LayoutParams = WindowManager.LayoutParams(
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

        // Set touch listeners for the floating view and the resize handle
        mFloatingView?.setOnTouchListener { _, event -> handleFloatingViewTouch(event) }
        resizeHandle?.setOnTouchListener { _, event -> handleResizeHandleTouch(event) }
    }

    private fun handleFloatingViewTouch(event: MotionEvent): Boolean {
        val params: WindowManager.LayoutParams = mFloatingView?.layoutParams as WindowManager.LayoutParams
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
                if (isResizing) {
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                } else {
                    // Not resizing, so it's dragging
                    params.x = (initialX + (event.rawX - initialTouchX)).toInt()
                    params.y = (initialY + (event.rawY - initialTouchY)).toInt()
                    mWindowManager?.updateViewLayout(mFloatingView, params)
                }
                return true
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
                initialWidth = mFloatingView?.width ?: 0
                initialHeight = mFloatingView?.height ?: 0
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
                    mFloatingView?.layoutParams?.width = newWidth
                    mFloatingView?.layoutParams?.height = newHeight
                    mWindowManager?.updateViewLayout(mFloatingView, mFloatingView?.layoutParams)
                    return true
                } else {
                    return false
                }
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
            mWindowManager?.removeView(mFloatingView)
        }
    }
}
