package com.example.joymap

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.joymap.Models.Requests.ChildLinkRequest
import com.example.joymap.Services.ChildrenAdapter
import com.example.joymap.Services.ParentApiService
import com.example.joymap.databinding.ActivityChildrenBinding

class ChildrenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChildrenBinding
    private lateinit var service: ParentApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChildrenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = ParentApiService(this);

        // Обработка нажатия на кнопку "Добавить ребенка"
        binding.btnScanQR.setOnClickListener {
            val code1 = binding.code1Input.text.toString()
            val code2 = binding.code2Input.text.toString()
            val parentId = getParentId()

            if (code1.isNotEmpty() && code2.isNotEmpty()) {
                val childLinkRequest = ChildLinkRequest(parentId, code1, code2)
                service.linkChildToParent(childLinkRequest) { response ->
                    runOnUiThread {
                        Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                        if (response.contains("success", true)) {
                            loadChildren()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Введите оба кода", Toast.LENGTH_SHORT).show()
            }
        }

        // Загрузка списка детей
        loadChildren()
    }

    private fun getParentId(): String {
        // Пример: Получение ParentId из SharedPreferences
        val sharedPreferences = getSharedPreferences("ParentAppPrefs", MODE_PRIVATE)
        val guid = sharedPreferences.getString("parentGuid", "")?.replace("\"","")
        Log.e("parentGUID", guid?:"not found")
        return  guid?: "not found"
    }

    private fun loadChildren() {
        val userId = getParentId()

        service.getUserData(userId) { user, error ->
            runOnUiThread {
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                } else {
                    user?.children?.let { children ->
                        // Передача полного списка объектов Child в адаптер
                        binding.listChildren.adapter = ChildrenAdapter(this, children)
                    }
                }
            }
        }
    }
}
