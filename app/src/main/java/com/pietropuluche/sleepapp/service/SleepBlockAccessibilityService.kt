package com.pietropuluche.sleepapp.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.pietropuluche.sleepapp.MainActivity
import com.pietropuluche.sleepapp.data.local.SleepStorage

class SleepBlockAccessibilityService : AccessibilityService() {

    private val storage by lazy { SleepStorage(applicationContext) }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return
        if (!storage.isPackageBlocked(packageName)) return

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("blocked_package", packageName)
        }
        startActivity(intent)
    }

    override fun onInterrupt() = Unit
}
