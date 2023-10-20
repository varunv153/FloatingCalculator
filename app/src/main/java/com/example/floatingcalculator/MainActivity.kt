package com.example.floatingcalculator


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Settings.canDrawOverlays(this)) {
            startCalculatorService()
        } else {
            //TODO : Use ActivityResultContracts.RequestPermission instead of ActivityResultContracts.StartActivityForResult
            val requestOverlayPermissionLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
                if (Settings.canDrawOverlays(this)) {
                    startCalculatorService()
                } else {
                    setContentView(R.layout.activity_main)
                }
            }
            requestOverlayPermissionLauncher.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }

    private fun startCalculatorService() {
        startForegroundService(Intent(this, FloatingWindowService::class.java))
        finish()
    }
}