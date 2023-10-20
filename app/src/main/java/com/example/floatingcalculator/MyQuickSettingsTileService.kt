package com.example.floatingcalculator

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class MyQuickSettingsTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile
        if (tile != null) {
            tile.contentDescription = "My Quick Settings Tile" // Set a description
            tile.state = Tile.STATE_ACTIVE // Set the initial state (active or inactive)
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        // Define the action when the tile is clicked
        // You can launch your app or perform a specific action here
        // For example, launching an activity:
        val intent = Intent(this@MyQuickSettingsTileService, FloatingWindowService::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startForegroundService(intent)
    }
}