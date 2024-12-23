package com.example.joymap.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.joymap.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PersistentService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Настройка логики фоновой работы
        startBackgroundTasks()
        return START_STICKY
    }

    private fun startBackgroundTasks() {
        // Подключение к серверу и обновление карты
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    // Например, отправляем запрос к серверу
                    updateDataOnServer()
                    delay(5000) // Обновление каждые 5 секунд
                } catch (e: Exception) {
                    Log.e("PersistentService", "Error upload: ${e.message}")
                }
            }
        }
    }

    private fun updateDataOnServer() {
        // Логика подключения к серверу
        Log.d("PersistentService", "Upload data in server")
        // Пример: используйте Retrofit для отправки данных
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "PersistentChannel")
            .setContentTitle("Фоновая работа")
            .setContentText("Приложение работает в фоновом режиме")
            .setSmallIcon(R.drawable.ic_notification) // Иконка для уведомления
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "PersistentChannel",
                "Фоновая работа",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
