package com.pinehotel.hospitality

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pinehotel.hospitality.databinding.ActivitySetupBinding
import com.pinehotel.hospitality.utils.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferenceManager = PreferenceManager(this)

        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pre-fill existing room number if available
        lifecycleScope.launch {
            val savedRoom = preferenceManager.roomNumberFlow.first()
            if (!savedRoom.isNullOrEmpty()) {
                binding.etRoomNumber.setText(savedRoom)
            }
        }

        binding.btnSave.setOnClickListener {
            val roomNumber = binding.etRoomNumber.text.toString().trim()
            if (roomNumber.isNotEmpty()) {
                saveAndFinish(roomNumber)
            } else {
                Toast.makeText(this, "Please enter a valid room number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAndFinish(roomNumber: String) {
        lifecycleScope.launch {
            preferenceManager.saveRoomNumber(roomNumber)
            startActivity(Intent(this@SetupActivity, MainActivity::class.java))
            finish()
        }
    }
}
