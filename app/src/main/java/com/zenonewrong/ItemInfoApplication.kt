package com.zenonewrong

import android.app.Application
import com.zenonewrong.notification.ExpiryNotificationScheduler

class ItemInfoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        ExpiryNotificationScheduler.disableAll(this)
    }
}
