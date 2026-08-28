package com.pinehotel.hospitality.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pinehotel.hospitality.databinding.ActivityServicesBinding

class ServicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}