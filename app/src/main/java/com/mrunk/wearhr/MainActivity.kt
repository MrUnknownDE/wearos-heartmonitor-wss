package com.mrunk.wearhr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mrunk.wearhr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val requestPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            binding.statusText.text = "Permission denied"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputUrl.setText(Prefs.getUrl(this))
        binding.inputToken.setText(Prefs.getJwt(this))

        binding.btnSave.setOnClickListener {
            Prefs.set(this, binding.inputUrl.text.toString(), binding.inputToken.text.toString())
            binding.statusText.text = "Saved"
        }

        binding.btnStart.setOnClickListener {
            ensurePermissionsAndStart()
        }

        binding.btnStop.setOnClickListener {
            stopService(Intent(this, HrStreamService::class.java))
            binding.statusText.text = getString(R.string.status_disconnected)
        }
    }

    private fun ensurePermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            requestPerm.launch(Manifest.permission.BODY_SENSORS)
            return
        }
        startForegroundService(Intent(this, HrStreamService::class.java))
        binding.statusText.text = "Starting…"
    }
}