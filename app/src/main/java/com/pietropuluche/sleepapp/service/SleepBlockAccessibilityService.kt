package com.pietropuluche.sleepapp.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.pietropuluche.sleepapp.MainActivity
import com.pietropuluche.sleepapp.data.local.SleepStorage

class SleepBlockAccessibilityService : AccessibilityService() {

    private val storage by lazy { SleepStorage(applicationContext) }
    private var lastBlockedPackage: String? = null
    private var lastBlockedAtMillis: Long = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (packageName == applicationContext.packageName) return
        val now = System.currentTimeMillis()
        if (!storage.isPackageBlocked(packageName, now)) return
        if (lastBlockedPackage == packageName && now - lastBlockedAtMillis < 1_500L) return
        lastBlockedPackage = packageName
        lastBlockedAtMillis = now

        performGlobalAction(GLOBAL_ACTION_HOME)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                putExtra(EXTRA_BLOCKED_PACKAGE, packageName)
            }
            startActivity(intent)
        }, 120L)
    }

    override fun onInterrupt() = Unit

    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "blocked_package"
    }
}
