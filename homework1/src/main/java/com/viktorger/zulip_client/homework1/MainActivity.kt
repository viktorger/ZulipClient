package com.viktorger.zulip_client.homework1

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.viktorger.zulip_client.homework1.common.CONTACT_ARRAY
import com.viktorger.zulip_client.homework1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val adapter: ContactAdapter by lazy { ContactAdapter() }
    private val secondActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            binding.btnMain.visibility = View.GONE
            binding.rvMain.visibility = View.VISIBLE
            val data = result.data?.getStringArrayExtra(CONTACT_ARRAY)
            data?.let {
                adapter.submitList(it.toList())
            }
        } else {
            Toast.makeText(this, "Ошибка при получении контактов", Toast.LENGTH_LONG)
        }
    }

    private val permissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            secondActivityResult.launch(Intent(this, SecondActivity::class.java))
        } else {
            Toast.makeText(this, "Ок, сиди смотри на кнопку", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMain.adapter = adapter
        binding.btnMain.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    secondActivityResult.launch(Intent(this, SecondActivity::class.java))
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.READ_CONTACTS) -> {
                    Toast.makeText(
                        this,
                        "Разрешите приложению доступ к контактам через настройки",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    permissionResult.launch(android.Manifest.permission.READ_CONTACTS)
                }
            }
        }
    }
}