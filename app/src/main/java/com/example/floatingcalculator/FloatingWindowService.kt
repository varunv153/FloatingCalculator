package com.example.floatingcalculator;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button
import com.example.floatingcalculator.R;

class FloatingWindowService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    private var initialX: Int = 0
    private var initialY: Int = 0
    private var initialTouchX: Float = 0.0f
    private var initialTouchY: Float = 0.0f
    private var isResizing = false
    private var resizeDirection = ResizeDirection.NONE
    private val MIN_WINDOW_SIZE = 100
    private var initialWidth: Int = 0
    private var initialHeight: Int = 0

    enum class ResizeDirection {
        NONE, LEFT, RIGHT, TOP, BOTTOM, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Inflate the layout for the floating window
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null)

        // Find the resize handle view by its ID
        val resizeHandle = mFloatingView?.findViewById<Button>(R.id.resize_handle)

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

        // Set touch listener for the floating view to enable dragging
        mFloatingView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Record the initial touch and view position
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isResizing) {
                        val deltaX = event.rawX - initialTouchX
                        val deltaY = event.rawY - initialTouchY
                        resizeWindow(params, deltaX, deltaY)
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                    } else {
                        // Not resizing, so it's dragging
                        params.x = (initialX + (event.rawX - initialTouchX)).toInt()
                        params.y = (initialY + (event.rawY - initialTouchY)).toInt()
                        mWindowManager?.updateViewLayout(mFloatingView, params)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    isResizing = false
                    resizeDirection = ResizeDirection.NONE
                    true
                }
                else -> false
            }
        }

        // Set touch listener for the resize handle
        resizeHandle?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Record the initial touch and view size
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    initialWidth = mFloatingView?.width ?: MIN_WINDOW_SIZE
                    initialHeight = mFloatingView?.height ?: MIN_WINDOW_SIZE
                    isResizing = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isResizing) {
                        val deltaX = event.rawX - initialTouchX
                        val deltaY = event.rawY - initialTouchY
                        val newWidth = Math.max(initialWidth + deltaX.toInt(), MIN_WINDOW_SIZE)
                        val newHeight = Math.max(initialHeight + deltaY.toInt(), MIN_WINDOW_SIZE)

                        // Update the view size
                        mFloatingView?.layoutParams?.width = newWidth
                        mFloatingView?.layoutParams?.height = newHeight
                        mWindowManager?.updateViewLayout(mFloatingView, mFloatingView?.layoutParams)
                        true
                    } else {
                        false
                    }
                }
                MotionEvent.ACTION_UP -> {
                    isResizing = false
                    true
                }
                else -> false
            }
        }
    }

    private fun resizeWindow(params: WindowManager.LayoutParams, deltaX: Float, deltaY: Float) {
        // Implement the resizing logic here, similar to your previous code
        // ...
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) {
            mWindowManager?.removeView(mFloatingView)
        }
    }
}
