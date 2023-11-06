package com.varunhobbydev.floatingcalculator

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class MyQuickSettingsTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile
        if (tile != null) {
            tile.contentDescription = "My Quick Settings Tile"
            tile.state = Tile.STATE_ACTIVE
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this@MyQuickSettingsTileService, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivityAndCollapse(intent)
    }
}