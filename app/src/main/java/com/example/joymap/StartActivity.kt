package com.example.joymap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.joymap.Services.ParentApiService
import com.example.joymap.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    private lateinit var parentService: ParentApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация ParentService
        parentService = ParentApiService(this)

        // Проверяем наличие GUID в памяти
        val guid = getGuidFromPreferences()
        if (guid == null) {
            // Если GUID отсутствует, вызываем createParent
            createParentAndSaveGuid()
        } else {
            Toast.makeText(this, "GUID already exists: $guid", Toast.LENGTH_SHORT).show()
        }

        // Устанавливаем действия на кнопки
        binding.btnMap.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnChildren.setOnClickListener {
            startActivity(Intent(this, ChildrenActivity::class.java))
        }
    }

    // Функция для вызова createParent и сохранения GUID
    @SuppressLint("HardwareIds")
    private fun createParentAndSaveGuid() {
        val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        parentService.createParent(deviceId) { response ->
            runOnUiThread {
                if (response.startsWith("Parent created successfully")) {
                    Toast.makeText(this, "Parent created and GUID saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: $response", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Функция для получения GUID из SharedPreferences
    private fun getGuidFromPreferences(): String? {
        val sharedPreferences = getSharedPreferences("ParentAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("parentGuid", null)
    }
}
