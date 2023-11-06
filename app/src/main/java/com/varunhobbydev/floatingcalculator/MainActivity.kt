package com.varunhobbydev.floatingcalculator


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG_LOG: String = "VarunCalci"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startApp(false)
    }

    private fun startApp(isOverlayPermissionRequested: Boolean) {
        if (Settings.canDrawOverlays(this)) {
            startForegroundService(Intent(this, FloatingWindowService::class.java))
            finish()
        } else if (isOverlayPermissionRequested) {
            setContentView(R.layout.activity_main)
        } else {
            val requestOverlayPermissionLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
                startApp(true)
            }
            requestOverlayPermissionLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }
}