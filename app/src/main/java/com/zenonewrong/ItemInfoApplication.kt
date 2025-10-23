package com.zenonewrong

import android.app.Application

class ItemInfoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}