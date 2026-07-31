package com.zenonewrong.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.zenonewrong.AppDatabase
import com.zenonewrong.MainActivity
import com.zenonewrong.R
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

private const val NOTIFICATION_ID = 1001

object ExpiryNotificationScheduler {
    private const val WORK_NAME = "expiry_notification_check"
    private const val PREFS_NAME = "expiry_notifications"
    private const val ENABLED_KEY = "enabled"
    private val reminderTags = listOf("yellow", "blue", "green")

    fun isEnabled(context: Context, tag: String): Boolean {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = enabledKey(tag)
        return if (preferences.contains(key)) {
            preferences.getBoolean(key, false)
        } else {
            preferences.getBoolean(ENABLED_KEY, false)
        }
    }

    fun setEnabled(context: Context, tag: String, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(enabledKey(tag), enabled)
            .apply()

        if (hasEnabledReminder(context)) {
            createNotificationChannel(context)
            schedule(context, ExistingPeriodicWorkPolicy.UPDATE)
        } else {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        }
    }

    fun restore(context: Context) {
        if (hasEnabledReminder(context)) schedule(context, ExistingPeriodicWorkPolicy.KEEP)
    }

    fun disableAll(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply {
                putBoolean(ENABLED_KEY, false)
                reminderTags.forEach { putBoolean(enabledKey(it), false) }
            }
            .apply()
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    fun enabledTags(context: Context): Set<String> =
        reminderTags.filterTo(mutableSetOf()) { isEnabled(context, it) }

    private fun hasEnabledReminder(context: Context): Boolean =
        reminderTags.any { isEnabled(context, it) }

    private fun enabledKey(tag: String) = "${ENABLED_KEY}_$tag"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            ExpiryNotificationWorker.CHANNEL_ID,
            "到期提醒",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "每天汇总即将到期的物品"
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun schedule(context: Context, policy: ExistingPeriodicWorkPolicy) {
        val delay = delayUntilNextReminder(ZonedDateTime.now())
        val request = PeriodicWorkRequestBuilder<ExpiryNotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, policy, request)
    }
}

class ExpiryNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val enabledTags = ExpiryNotificationScheduler.enabledTags(applicationContext)
        if (enabledTags.isEmpty() || !canNotify()) {
            return Result.success()
        }

        return try {
            val dao = AppDatabase.getDatabase(applicationContext).expiryReminderDao()
            val daysByTag = dao.getExpiryReminders().associate { it.tag to it.days }
            val upcomingCount = reminderRanges(daysByTag, enabledTags).sumOf { range ->
                dao.countItemsDueBetween(range.first, range.last + 1)
            }
            val summary = buildExpirySummary(upcomingCount)

            if (summary == null) {
                NotificationManagerCompat.from(applicationContext).cancel(NOTIFICATION_ID)
            } else {
                showNotification(summary)
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private fun canNotify(): Boolean {
        val permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        return permissionGranted && NotificationManagerCompat.from(applicationContext)
            .areNotificationsEnabled()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(summary: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("物品到期提醒")
            .setContentText(summary)
            .setStyle(NotificationCompat.BigTextStyle().bigText(summary))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "expiry_reminders"
    }
}

internal fun buildExpirySummary(upcomingCount: Int): String? = when {
    upcomingCount > 0 -> "即将到期 $upcomingCount 件"
    else -> null
}

internal fun reminderRanges(
    daysByTag: Map<String, Int>,
    enabledTags: Set<String>
): List<IntRange> = listOfNotNull(
    daysByTag["yellow"]?.let { "yellow" to (0 until it) },
    daysByTag["yellow"]?.let { start ->
        daysByTag["blue"]?.let { end -> "blue" to (start until end) }
    },
    daysByTag["blue"]?.let { start ->
        daysByTag["green"]?.let { end -> "green" to (start until end) }
    }
).filter { (tag, range) -> tag in enabledTags && !range.isEmpty() }
    .map { it.second }

internal fun delayUntilNextReminder(now: ZonedDateTime): Duration {
    var next = now.withHour(9).withMinute(0).withSecond(0).withNano(0)
    if (!next.isAfter(now)) next = next.plusDays(1)
    return Duration.between(now, next)
}
